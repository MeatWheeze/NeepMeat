package com.neep.neepmeat.plc.instruction;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.block.entity.PLCExecutor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ExecInstruction implements Instruction
{
    private final Argument target;
    private final Instruction instruction;

    public ExecInstruction(@Nullable Argument target, Instruction instruction)
    {
        this.target = target;
        this.instruction = instruction;
    }

    public ExecInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this.target = Argument.fromNbt(nbt.getCompound("target"));

        NbtCompound instructionNbt = nbt.getCompound("instruction");
        Identifier id = Identifier.tryParse(instructionNbt.getString("id"));
        InstructionProvider provider = Instructions.REGISTRY.get(id);
        if (provider != null)
            instruction = provider.createFromNbt(world, instructionNbt);
        else
            instruction = EMPTY;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtCompound instructionNbt = new NbtCompound();
        instructionNbt.putString("id", Instructions.REGISTRY.getId(instruction.getProvider()).toString());
        instruction.writeNbt(instructionNbt);

        nbt.put("instruction", instructionNbt);
        nbt.put("target", target.toNbt());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        if (plc instanceof PLCBlockEntity pbe)
        {
            if (pbe.getWorld().getBlockEntity(target.pos()) instanceof PLCExecutor executor)
            {
                executor.receiveInstruction(instruction);
            }
        }
        else
        {
            plc.raiseError(new PLC.Error("oh noes"));
        }
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.EXEC;
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        Argument target = parser.parseArgument(view);
        if (target == null)
            throw new NeepASM.ParseException("expected argument after exec");

        ParsedInstruction parsedInstruction = parser.parseInstruction(view);
        if (parsedInstruction == null)
            throw new NeepASM.ParseException("expected instruction after exec");

        return (world, source, program) ->
        {
            DummyProgram dummyProgram = new DummyProgram();

            parsedInstruction.build(world, source, dummyProgram);
            Instruction instruction = dummyProgram.get(0);

            if (instruction == EMPTY)
                throw new NeepASM.CompilationException("exec instruction is empty");

            if (instruction.getProvider().equals(Instructions.EXEC))
                throw new NeepASM.CompilationException("nested exec instructions not allowed");

            program.addBack(new ExecInstruction(target, instruction));
        };
    }

    private static class DummyProgram implements MutableProgram
    {
        public List<Instruction> instructions = Lists.newArrayList();

        @Override
        public NbtCompound writeNbt(NbtCompound nbt)
        {
            return nbt;
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {

        }

        @Override
        public void addBack(Instruction instruction)
        {
            instructions.add(instruction);
        }

        @Override
        public void insert(int index, Instruction instruction)
        {
            instructions.add(index, instruction);
        }

        @Override
        public void remove(int index)
        {
            instructions.remove(index);
        }

        @Override
        public void add(int selected, Instruction instruction)
        {
            if (selected >= instructions.size())
            {
                instructions.add(instruction);
            }
            else
            {
                instructions.add(selected + 1, instruction);
            }
        }

        @Override
        public void setDebugLine(int line)
        {

        }

        @Override
        public Instruction get(int index)
        {
            if (index < instructions.size())
            {
                return instructions.get(index);
            }
            return Instruction.end();
        }

        @Override
        public int size()
        {
            return instructions.size();
        }

        @Override
        public int getDebugLine(int counter)
        {
            return -1;
        }
    }
}
