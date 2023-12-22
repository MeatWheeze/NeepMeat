package com.neep.neepmeat.api.plc.robot;

import com.neep.neepmeat.api.plc.PLC;

public interface RobotAction
{
    boolean finished(PLC plc);

    void start(PLC plc);

    void tick(PLC plc);

    default boolean blocksController() { return true; }

    default void cancel(PLC plc) {};

//    @Override
//    default NbtCompound writeNbt(NbtCompound nbt) { return nbt; }
//
//    @Override
//    default void readNbt(NbtCompound nbt) {}
}
