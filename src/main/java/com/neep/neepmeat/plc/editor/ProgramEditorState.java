package com.neep.neepmeat.plc.editor;

import com.google.common.collect.Lists;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.PLCState;
import com.neep.neepmeat.plc.instruction.*;
import com.neep.neepmeat.plc.program.MutableProgram;
import com.neep.neepmeat.plc.program.PLCProgramImpl;
import com.neep.neepmeat.plc.program.PlcProgram;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProgramEditorState implements PLCState
{
    private final PLCBlockEntity parent;
    private MutableProgram program;
    @Nullable private InstructionBuilder instructionBuilder;
    private List<Listener> listeners = Lists.newArrayList();

    public ProgramEditorState(PLCBlockEntity parent)
    {
        this.parent = parent;
        program = new PLCProgramImpl(parent::getWorld);
    }

    @Override
    public void setInstructionBuilder(InstructionProvider provider)
    {
        instructionBuilder = provider.start((ServerWorld) parent.getWorld(), this::emitInstruction);
    }

    private void emitInstruction(Instruction instruction)
    {
        if (program != null)
        {
            program.addBack(instruction);
            sendProgram();
        }
    }

    @Override
    public void argument(Argument argument)
    {
        if (program != null && instructionBuilder != null)
        {
            instructionBuilder.argument(argument);
        }
    }

    public void delete(int index)
    {
        if (program != null)
        {
            program.remove(index);
            sendProgram();
        }
    }

    private void sendProgram()
    {
        if (!parent.getWorld().isClient() && parent.getRobot().getController() != null)
        {
            PLCSyncProgram.sendProgram((ServerPlayerEntity) parent.getRobot().getController(), parent, program);
        }
    }

    public void receiveProgram(NbtCompound nbt)
    {
        if (program == null)
        {
            program = new PLCProgramImpl(parent::getWorld);
        }
        program.readNbt(nbt);
        updateListeners();
    }

    public PlcProgram getProgram()
    {
        return program;
    }

    public void addListener(Listener listener)
    {
        this.listeners.add(listener);
    }

    private void updateListeners()
    {
        for (var listener : listeners)
        {
            listener.update(program);
        }
    }

    @FunctionalInterface
    public interface Listener
    {
        void update(PlcProgram program);
    }
}
