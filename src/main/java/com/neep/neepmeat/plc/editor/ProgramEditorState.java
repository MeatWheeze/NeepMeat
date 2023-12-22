package com.neep.neepmeat.plc.editor;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.instruction.*;
import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.network.plc.PLCErrorMessageS2C;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.PLCState;
import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.program.PLCProgramImpl;
import com.neep.neepmeat.api.plc.program.PlcProgram;
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
    @Nullable private InstructionProvider provider;
    private final List<Listener> listeners = Lists.newArrayList();
    private int selected;

    public ProgramEditorState(PLCBlockEntity parent)
    {
        this.parent = parent;
        program = new PLCProgramImpl(parent::getWorld);
    }

    @Override
    public void setInstructionBuilder(InstructionProvider provider)
    {
        this.provider = provider;
        instructionBuilder = provider.start((ServerWorld) parent.getWorld(), this::emitInstruction);
    }

    private void emitInstruction(Instruction instruction)
    {
        if (program != null)
        {
            program.add(selected, instruction);
//            program.addBack(instruction);
            selected = Math.min(program.size() - 1, selected + 1);
            sendProgram();
        }
    }

    @Override
    public void argument(Argument argument)
    {
        if (program != null && instructionBuilder != null)
        {
            try
            {
                instructionBuilder.argument(argument);
            }
            catch (InstructionException e)
            {
                PLCErrorMessageS2C.send((ServerPlayerEntity) parent.getRobot().getController(), e.getMessage());
            }
        }
    }

    @Override
    public RecordMode getMode()
    {
        return RecordMode.RECORD;
    }

    @Override
    public int getArgumentCount()
    {
        if (instructionBuilder == null)
            return 0;

        return instructionBuilder.argumentCount();
    }

    @Override
    public int getMaxArguments()
    {
        if (provider == null)
            return 0;

        return provider.maxArguments();
    }

    public void delete(int index)
    {
        if (program != null)
        {
            program.remove(index);
            if (selected >= program.size())
            {
                selected = program.size() - 1;
            }
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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        program.writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        program.readNbt(nbt);
    }

    public int getSelected()
    {
        return selected;
    }

    public void setSelected(int selected)
    {
        this.selected = selected;
    }

    @FunctionalInterface
    public interface Listener
    {
        void update(PlcProgram program);
    }
}
