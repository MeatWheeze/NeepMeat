package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

// Branch if true
public class BIFInstruction implements Instruction
{
    private final Label label;

    public BIFInstruction(Label label)
    {
        Objects.requireNonNull(label);
        this.label = label;
    }

    public BIFInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this.label = new Label(nbt.getString("name"), nbt.getInt("target"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putString("name", label.name());
        nbt.putInt("target", label.index());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        if (plc.variableStack().popInt() <= 0)
            plc.setCounter(label.index());
        else
            plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.BIF;
    }
}
