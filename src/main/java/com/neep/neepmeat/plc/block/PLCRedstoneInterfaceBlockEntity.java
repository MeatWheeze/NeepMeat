package com.neep.neepmeat.plc.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class PLCRedstoneInterfaceBlockEntity extends BlockEntity implements RedstoneInterface
{
    private int outStrength;
    private int received;

    public PLCRedstoneInterfaceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public int getReceivedStrength()
    {
        return received;
    }

    @Override
    public void setEmittedStrength(int strength)
    {
        if (outStrength != strength)
        {
            outStrength = strength;
            emitUpdate();
        }
    }

    private void emitUpdate()
    {
        if (world != null)
        {
            world.updateNeighbors(pos, getCachedState().getBlock());
        }
    }

    public void updateReceived(int received)
    {
        this.received = received;
    }
}
