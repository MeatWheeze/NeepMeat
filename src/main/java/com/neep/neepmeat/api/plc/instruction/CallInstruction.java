package com.neep.neepmeat.api.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class CallInstruction implements Instruction
{
    private final Label label;

    public CallInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
    {
        this.label = new Label(nbt.getString("name"), nbt.getInt("target"));
    }

    public CallInstruction(@NotNull Label label)
    {
        Objects.requireNonNull(label);
        this.label = label;
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
        plc.pushCall(plc.counter());
        plc.setCounter(label.index());
    }

    @Override
    public void cancel(PLC plc)
    {

    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.CALL;
    }
}
