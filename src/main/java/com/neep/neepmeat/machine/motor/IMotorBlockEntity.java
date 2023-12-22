package com.neep.neepmeat.machine.motor;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.lwjgl.system.CallbackI;

public interface IMotorBlockEntity
{
    default void update(ServerWorld world, BlockPos pos, BlockPos fromPos, BlockState state)
    {
        Direction facing = state.get(BaseFacingBlock.FACING);
        if ((world.getBlockEntity(pos.offset(facing)) instanceof IMotorisedBlock block))
        {
            setConnectedBlock(BlockApiCache.create(MeatLib.VOID_LOOKUP, world, pos.offset(facing)));
        }
    }

    void setConnectedBlock(BlockApiCache<Void, Void> motorised);

    float getRotorAngle();

    BlockApiCache<Void, Void> getConnectedBlock();
}
