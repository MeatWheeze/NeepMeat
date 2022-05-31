package com.neep.neepmeat.entity;

import com.neep.neepmeat.blockentity.fluid.TankBlockEntity;
import com.neep.neepmeat.fluid_transfer.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Iterator;

@SuppressWarnings("UnstableApiUsage")
public class TankMinecartEntity extends AbstractMinecartEntity implements Storage<FluidVariant>
{
    public static final String AMOUNT = "amount";
    public static final String RESOURCE = "resource";

    protected FluidVariant resource = FluidVariant.blank();

    WritableFluidBuffer buffer = new WritableFluidBuffer(null, 8 * FluidConstants.BUCKET);

    public TankMinecartEntity(EntityType<?> entityType, World world)
    {
        super(entityType, world);
        this.setCustomBlock(NMBlocks.TANK.getDefaultState());
        this.setCustomBlockPresent(true);
    }

    public TankMinecartEntity(World world, double x, double y, double z)
    {
        super(NMEntities.TANK_MINECART, world, x, y, z);
        this.setCustomBlock(NMBlocks.TANK.getDefaultState());
        this.setCustomBlockPresent(true);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        buffer.writeNBT(nbt);

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        buffer.readNBT(nbt);
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

    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand)
    {
        if (!getEntityWorld().isClient)
        {
            TankBlockEntity.showContents((ServerPlayerEntity) player, world, getBlockPos(), buffer);
        }
        return ActionResult.SUCCESS;
    }
}
