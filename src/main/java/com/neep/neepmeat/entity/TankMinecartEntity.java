package com.neep.neepmeat.entity;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.transport.machine.fluid.TankBlockEntity;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class TankMinecartEntity extends AbstractMinecartEntity implements Storage<FluidVariant>, FluidBuffer.FluidBufferProvider
{
    public static final String AMOUNT = "amount";
    public static final String RESOURCE = "resource";

    protected FluidVariant resource = FluidVariant.blank();

    WritableSingleFluidStorage buffer = new WritableSingleFluidStorage(8 * FluidConstants.BUCKET, null);

    public TankMinecartEntity(EntityType<?> entityType, World world)
    {
        super(entityType, world);
        this.setCustomBlock(NMBlocks.GLASS_TANK.getDefaultState());
        this.setCustomBlockPresent(true);
    }

    public TankMinecartEntity(World world, double x, double y, double z)
    {
        super(NMEntities.TANK_MINECART, world, x, y, z);
        this.setCustomBlock(NMBlocks.GLASS_TANK.getDefaultState());
        this.setCustomBlockPresent(true);
    }

    public static void renderFluidCuboid(VertexConsumerProvider vertices, MatrixStack matrices, FluidVariant fluid, float startXYZ, float startXZ, float endXZ, float endY, float scaleY)
    {
        Sprite sprite = FluidVariantRendering.getSprite(fluid);
        VertexConsumer consumer = vertices.getBuffer(RenderLayer.getTranslucent());
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        int col = FluidVariantRendering.getColor(fluid);

        // Magic colourspace transformation copied from Modern Industrialisation
        float r = ((col >> 16) & 255) / 256f;
        float g = ((col >> 8) & 255) / 256f;
        float b = (col & 255) / 256f;

        if (fluid.isBlank() || scaleY == 0)
        {
            return;
        }

        float startY = startXYZ;
//        float dist = startY + (endY - startY) * scaleY;
        float dist = startY + (endY - startY) * scaleY;
        if (FluidVariantAttributes.isLighterThanAir(fluid))
        {
            matrices.translate(1, 1, 0);
            matrices.scale(-1, -1, 1);
        }

        for (Direction direction : Direction.values())
        {
            QuadEmitter emitter = renderer.meshBuilder().getEmitter();

            if (direction.getAxis().isVertical())
            {
                emitter.square(direction, endXZ, endXZ, startXZ, startXZ, direction == Direction.UP ? 1 - dist : startY);
            }
            else
            {
                // Nasty bodge because I can't be bothered to fix this
                emitter.square(direction, endXZ, startXYZ, startXZ, dist, endXZ);
            }
//            emitter.square(direction, 0.1f, 0.1f, 0.9f, 0.9f - fill, 0.9f);

            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
//            emitter.spriteBake(0, sprite, MutableQuadView.BAKE_ROTATE_90);
            emitter.spriteColor(0, -1, -1, -1, -1);

            consumer.quad(matrices.peek(), emitter.toBakedQuad(0, sprite, false), r, g, b, 0x00F0_00F0, OverlayTexture.DEFAULT_UV);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        buffer.writeNbt(nbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        buffer.readNbt(nbt);
    }

    @Override
    public Type getMinecartType()
    {
        return null;
    }

    @Override
    public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        return buffer.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction)
    {
        return buffer.extract(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<FluidVariant>> iterator(TransactionContext transaction)
    {
        return buffer.iterator(transaction);
    }

    @Override
    public void dropItems(DamageSource damageSource)
    {
        this.remove(Entity.RemovalReason.KILLED);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemStack itemStack = new ItemStack(Items.MINECART);
            if (this.hasCustomName()) {
                itemStack.setCustomName(this.getCustomName());
            }
            this.dropStack(itemStack);
            this.dropStack(NMBlocks.GLASS_TANK.asItem().getDefaultStack());
        }
    }

    @Override
    public ItemStack getPickBlockStack()
    {
        return NMItems.TANK_MINECART.getDefaultStack();
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand)
    {
        if (WritableFluidBuffer.handleInteract(buffer, world, player, hand))
        {
            return ActionResult.SUCCESS;
        }
        else if (!getEntityWorld().isClient)
        {
            TankBlockEntity.showContents((ServerPlayerEntity) player, world, getBlockPos(), buffer);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public SingleVariantStorage<FluidVariant> getBuffer(Direction direction)
    {
        return buffer;
    }
}
