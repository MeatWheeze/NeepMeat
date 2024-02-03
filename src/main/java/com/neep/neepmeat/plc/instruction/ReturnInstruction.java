package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class ReturnInstruction implements Instruction
{
    public ReturnInstruction()
    {

    }

    public ReturnInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
    {

    }

    @Override
    public void start(PLC plc)
    {
        int counter = plc.popCall();
        plc.setCounter(counter);
    }

    @Override
    public void cancel(PLC plc)
    {

    }

    @Override
    public InstructionProvider getProvider()
    {
        return Instructions.RET;
    }
}
