package com.neep.neepmeat.plc.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class PLCRedstoneInterfaceBlockEntity extends BlockEntity implements RedstoneInterface
{
    private int emitted;
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

    public int getEmittedStrength()
    {
        return emitted;
    }

    @Override
    public void setEmittedStrength(int strength)
    {
        if (emitted != strength)
        {
            emitted = strength;
            markDirty();
            emitUpdate();
        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.emitted = nbt.getInt("emitted");
        this.received = nbt.getInt("received");
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("emitted", emitted);
        nbt.putInt("received", received);
    }

    private void emitUpdate()
    {
        if (world != null)
        {
            world.createAndScheduleBlockTick(pos, getCachedState().getBlock(), 1);
//            world.updateNeighbors(pos, getCachedState().getBlock());
        }
    }

    public void updateReceived(int received)
    {
        this.received = received;
        markDirty();
    }
}
