package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class JumpInstruction implements Instruction
{
    private final Label label;

    public JumpInstruction(Label label)
    {
        this.label = label;
    }

    public JumpInstruction(Supplier<World> worldSupplier, List<Argument> arguments)
    {
        throw new NotImplementedException();
    }

    public JumpInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
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
    public void readNbt(NbtCompound nbt)
    {

    }

    @Override
    public boolean canStart(PLC plc)
    {
        return true;
    }

    @Override
    public void start(PLC plc)
    {
        plc.setCounter(label.index());
    }

    @Override
    public void cancel(PLC plc)
    {

    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.JUMP;
    }
}
