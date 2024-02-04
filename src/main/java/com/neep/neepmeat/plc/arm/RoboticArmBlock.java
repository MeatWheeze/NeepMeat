package com.neep.neepmeat.plc.arm;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.storage.MeatlibStorageUtil;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.machine.surgical_controller.PLCBlock;
import com.neep.neepmeat.plc.PLCBlocks;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RoboticArmBlock extends BaseBlock implements BlockEntityProvider
{

    public RoboticArmBlock(String registryName, Settings settings)
    {
        super(registryName, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return PLCBlocks.ROBOTIC_ARM_ENTITY.instantiate(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockEntity(pos) instanceof RoboticArmBlockEntity be)
        {
            be.dumpStored();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, PLCBlocks.ROBOTIC_ARM_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(),
                (world1, pos, state1, blockEntity) -> blockEntity.clientTick(), world);
    }
}
