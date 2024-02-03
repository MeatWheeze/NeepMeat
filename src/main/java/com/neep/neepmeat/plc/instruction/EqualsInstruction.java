package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.neepasm.compiler.variable.Variable;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EqualsInstruction implements Instruction
{
    public EqualsInstruction() { }

    public EqualsInstruction(Supplier<World> world, NbtCompound nbt) { }

    @Override
    public void start(PLC plc)
    {
        Variable<?> v1 = plc.variableStack().pop();;
        Variable<?> v2 = plc.variableStack().pop();

        if (v1.notEmpty() && v1.equals(v2))
            plc.flag(1);
        else
            plc.flag(0);

        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.EQUAL;
    }
}
