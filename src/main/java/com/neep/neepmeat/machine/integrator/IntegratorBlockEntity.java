package com.neep.neepmeat.machine.integrator;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.api.data.DataUtil;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class IntegratorBlockEntity extends SyncableBlockEntity implements Integrator, GeoBlockEntity
{
    protected int growthTimeRemaining = 1000;
    protected final IntegratorStorage storage;

    protected BlockPos lookTarget = new BlockPos(0, 0, 0);
    public boolean isMature = false;
    public float facing = 0f;
    public float targetFacing = 0f;

    public static final long MAX_DATA = 8 * DataUtil.GIEB;
    protected long data;

    protected SnapshotParticipant<Long> dataSnapshot = new SnapshotParticipant<>()
    {
        @Override
        protected Long createSnapshot()
        {
            return data;
        }

        @Override
        protected void readSnapshot(Long snapshot)
        {
            data = snapshot;
        }

        @Override
        protected void onFinalCommit()
        {
            super.onFinalCommit();
            sync();
        }
    };

    private final AnimatableInstanceCache instanceCache = GeckoLibUtil.createInstanceCache(this);
    private boolean hatching;

    public IntegratorBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.INTEGRATOR, pos, state);
        this.storage = new IntegratorStorage(this);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return createNbt();
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        tag.putInt("growth_remaining", growthTimeRemaining);
        tag.putBoolean("fully_grown", isMature);
        tag.put("lookTarget", NbtHelper.fromBlockPos(lookTarget));
        tag.putLong("data", data);
        storage.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        growthTimeRemaining = tag.getInt("growth_remaining");
        isMature = tag.getBoolean("fully_grown");
        this.lookTarget = NbtHelper.toBlockPos(tag.getCompound("lookTarget"));
        this.data = tag.getLong("data");
        storage.readNbt(tag);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, IntegratorBlockEntity be)
    {
        if (be.canGrow())
        {
            be.grow();
            be.sync();
        }

        if (be.isMature)
        {
            be.data = Math.min(MAX_DATA, be.data + 1);
            if ((world.getTime() % 20) == 0) be.sync();

            if ((world.getTime() % 60) == 0) be.pointToEntity();
        }
    }

    public void pointToEntity()
    {
        if (world.getBlockEntity(pos) instanceof IntegratorBlockEntity be)
        {
            Box box = new Box(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3);
            List<Entity> players = be.getWorld().getEntitiesByType(TypeFilter.instanceOf(Entity.class), box, (e) -> true);
            if (players.size() > 0)
            {
                be.setLookPos(players.get(0).getBlockPos());
                sync();
            }
        }
    }

    public boolean canGrow()
    {
        return !isMature && storage.immatureStorage.getAmount() > 0;
    }

    public void grow()
    {
        if (storage.immatureStorage.getAmount() >= storage.immatureStorage.getCapacity())
            --growthTimeRemaining;

        if (growthTimeRemaining <= 0)
        {
            isMature = true;
        }
    }

    public static final Map<Item, Integer> DATA_MAP = Map.of(
            NMItems.WHISPER_FLOUR, 500
    );

    public boolean takeFromHand(PlayerEntity player, Hand hand)
    {
        ItemStack handStack = player.getStackInHand(hand);
        Integer inc = DATA_MAP.get(handStack.getItem());
        if (inc != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                float ins = insertEnlightenment(inc, transaction);
                if (ins == inc)
                {
                    transaction.commit();
                    world.playSound(null, pos, SoundEvents.ENTITY_HORSE_EAT, SoundCategory.BLOCKS, 1, 1);
                    handStack.decrement(1);
                    return true;
                }
                else transaction.abort();

            }
        }
        return false;
    }

    public void showContents(ServerPlayerEntity player)
    {
        if (!canEnlighten())
            player.sendMessage(Text.of("Blood: " + storage.immatureStorage.getAmount() / (FluidConstants.BUCKET) * 100 + "%"), true);
        else
        {
            player.sendMessage(Text.translatable("message." + NeepMeat.NAMESPACE + ".integrator.data",
                    DataUtil.formatData(data),
                    DataUtil.formatData(MAX_DATA)), true);
        }

    }

    private <E extends GeoBlockEntity> PlayState predicate(AnimationState<E> event)
    {
        event.getController().transitionLength(20);
        if (this.hatching)
        {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.integrator.hatch"));
            hatching = false;
        }
        else
        {
            event.getController().setAnimation(RawAnimation.begin().thenPlay("animation.integrator.idle"));
        }

        return PlayState.CONTINUE;
    }

    public Storage<FluidVariant> getStorage(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return storage.getFluidStorage(world, pos, state, direction);
    }

    public Storage<ItemVariant> getItemStorage(Direction direction)
    {
        return storage.itemStorage;
    }

    public float insertEnlightenment(float maxAmount, TransactionContext transaction)
    {
        float inserted = Math.min(MAX_DATA - data, maxAmount);
        if (inserted > 0)
        {
            dataSnapshot.updateSnapshots(transaction);
            data += inserted;
            return inserted;
        }
        return 0;
    }

    @Override
    public boolean canEnlighten()
    {
        return isMature;
    }

    @Override
    public BlockPos getBlockPos() { return getPos(); }


    @Override
    public void setLookPos(BlockPos pos)
    {
        this.lookTarget = pos;
        sync();
    }

    @Override
    public void spawnBeam(World world, BlockPos pos)
    {
        Integrator.spawnBeam((ServerWorld) world, getPos().up(), pos);
        world.playSound(null, pos, NMSounds.COSMIC_BEAM, SoundCategory.BLOCKS, 10, 0.8f);
    }

    @Override
    public long getData(DataVariant variant)
    {
        return data;
    }

    @Override
    public float extract(DataVariant variant, long amount, TransactionContext transaction)
    {
        long extracted = Math.min(amount, data);
        if (extracted > 0)
        {
            dataSnapshot.updateSnapshots(transaction);
            data -= extracted;
            return extracted;
        }
        return 0;
    }

    public Vec3d getLookTarget()
    {
        return Vec3d.ofCenter(lookTarget);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers)
    {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return instanceCache;
    }
}
