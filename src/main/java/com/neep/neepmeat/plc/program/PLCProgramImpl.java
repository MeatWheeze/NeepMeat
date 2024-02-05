package com.neep.neepmeat.plc.program;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PLCProgramImpl implements MutableProgram
{
    private final Supplier<World> worldSupplier;
    protected ArrayList<Instruction> instructions = Lists.newArrayList();

    private final IntList debugLines = new IntArrayList();
    private int debugLine = -1;

    public PLCProgramImpl(Supplier<World> worldSupplier)
    {
        this.worldSupplier = worldSupplier;
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
        if (counter == -1 || counter >= debugLines.size())
            return -1;

        return debugLines.getInt(counter);
    }

    @Override
    public void addBack(Instruction instruction)
    {
        debugLines.add(debugLine);
        instructions.add(instruction);
    }

    @Override
    public void insert(int index, Instruction instruction)
    {
        debugLines.add(index, debugLine);
        instructions.add(index, instruction);
    }

    @Override
    public void remove(int index)
    {
        if (index < instructions.size() && index >= 0)
        {
            instructions.remove(index);
            debugLines.rem(index);
        }
    }

    @Override
    public void add(int selected, Instruction instruction)
    {
        if (selected >= instructions.size())
        {
            instructions.add(instruction);
            debugLines.add(debugLine);
        }
        else
        {
            instructions.add(selected + 1, instruction);
            debugLines.add(selected + 1, debugLine);
        }
    }

    @Override
    public void setDebugLine(int line)
    {
        debugLine = line;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        for (int i = 0; i < instructions.size(); ++i)
        {
            Instruction instruction = instructions.get(i);
            int line = debugLines.getInt(i);

            NbtCompound instructionNbt = new NbtCompound();
            instruction.writeNbt(instructionNbt);
            instructionNbt.putInt("debug_line", line);
            instructionNbt.putString("id", Instructions.REGISTRY.getId(instruction.getProvider()).toString());
            list.add(instructionNbt);
        }
        nbt.put("instructions", list);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        instructions.clear();

        NbtList list = nbt.getList("instructions", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); ++i)
        {
            NbtCompound instructionNbt = list.getCompound(i);

            int line = instructionNbt.getInt("debug_line");
            Identifier id = Identifier.tryParse(instructionNbt.getString("id"));
            InstructionProvider provider = Instructions.REGISTRY.get(id);
            if (provider != null)
            {
                instructions.add(provider.createFromNbt(worldSupplier, instructionNbt));
                debugLines.add(line);
            }
        }
    }
}
