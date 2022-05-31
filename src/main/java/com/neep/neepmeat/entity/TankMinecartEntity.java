package com.neep.neepmeat.entity;

import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class TankMinecartEntity extends AbstractMinecartEntity
{
    public static final String AMOUNT = "amount";
    public static final String RESOURCE = "resource";

    protected long capacity;
    protected long amount;
    protected FluidVariant resource = FluidVariant.blank();

    public final SingleVariantStorage<FluidVariant> fluidStorage = new SingleVariantStorage<>()
    {
        @Override
        protected FluidVariant getBlankVariant()
        {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant)
        {
            return 8 * FluidConstants.BUCKET;
        }

        @Override
        protected void onFinalCommit()
        {
        }
    };

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

        nbt.putLong(AMOUNT, amount);
        nbt.put(RESOURCE, resource.toNbt());

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);

        this.amount = nbt.getLong(AMOUNT);
        this.resource = FluidVariant.fromNbt(nbt.getCompound(RESOURCE));
    }

    @Override
    public Type getMinecartType()
    {
        return null;
    }

}
