package com.neep.neepmeat.machine.alloy_kiln;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.IHeatable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AlloyKilnBlockEntity extends SyncableBlockEntity implements IHeatable
{
    public AlloyKilnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public AlloyKilnBlockEntity(BlockPos pos, BlockState state)
    {
        super(NMBlockEntities.ALLOY_KILN, pos, state);
    }

    public static <E extends BlockEntity> void serverTick(World world, BlockPos pos, BlockState state, AlloyKilnBlockEntity be)
    {

    }

    @Override
    public void setBurning()
    {

    }

    @Override
    public void updateState(World world, BlockPos pos, BlockState oldState)
    {

    }
}
