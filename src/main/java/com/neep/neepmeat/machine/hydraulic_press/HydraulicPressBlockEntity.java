package com.neep.neepmeat.machine.hydraulic_press;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class HydraulicPressBlockEntity extends SyncableBlockEntity
{
    public static final long EXTEND_AMOUNT = FluidConstants.BUCKET;
    public static final int TICKS_EXTENDED = 30;
    
    protected boolean recipeControlled = true;
    protected short recipeState;
    protected int extensionTicks;

    public float renderExtension;

    public WritableSingleFluidStorage fluidStorage = new WritableSingleFluidStorage(EXTEND_AMOUNT, this::sync)
    {
        @Override
        protected boolean canInsert(FluidVariant variant)
        {
            return super.canInsert(variant) && variant.isOf(Fluids.WATER) && (!recipeControlled || recipeState == 0);
        }

        @Override
        protected boolean canExtract(FluidVariant variant)
        {
            return super.canExtract(variant) && variant.isOf(Fluids.WATER) && (!recipeControlled || recipeState == 2);
        }
    };

    public HydraulicPressBlockEntity(BlockEntityType<HydraulicPressBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public HydraulicPressBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.HYDRAULIC_PRESS, pos, state);
    }

    public void tick()
    {
        if (recipeState == 0 && fluidStorage.getAmount() >= EXTEND_AMOUNT)
        {
            this.extensionTicks = 0;
            this.recipeState = 1;
        }
        if (recipeState == 1)
        {
            extensionTicks = Math.min(TICKS_EXTENDED, extensionTicks + 1);
            if (extensionTicks >= TICKS_EXTENDED)
            {
                this.recipeState = 2;
            }
        }
        if (recipeState == 2 && fluidStorage.getAmount() == 0)
        {
            this.recipeState = 0;
        }
    }
    
    public WritableSingleFluidStorage getStorage(Direction direction)
    {
        Direction facing = getCachedState().get(HydraulicPressBlock.FACING);
        return direction == null || direction == facing || direction == facing.getOpposite() ? fluidStorage : null;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        fluidStorage.writeNbt(nbt);
        nbt.putShort("recipeState", recipeState);
        nbt.putBoolean("recipeControlled", recipeControlled);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        fluidStorage.readNbt(nbt);
        this.recipeState = nbt.getShort("recipeState");
        this.recipeControlled = nbt.getBoolean("recipeControlled");
    }
}
