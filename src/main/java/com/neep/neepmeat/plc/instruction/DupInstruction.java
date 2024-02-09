package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DupInstruction implements Instruction
{
    public DupInstruction()
    {

    }

    public DupInstruction(Supplier<World> world, NbtCompound nbt)
    {
    }

    @Override
    public void start(PLC plc)
    {
        plc.variableStack().push(plc.variableStack().peekInt(0));
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.DUP;
    }
}
