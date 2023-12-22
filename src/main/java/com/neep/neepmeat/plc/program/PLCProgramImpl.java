package com.neep.neepmeat.plc.program;

import com.google.common.collect.Lists;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.opcode.InstructionProvider;
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
    protected ArrayList<PLCInstruction> instructions = Lists.newArrayList();

    public PLCProgramImpl(Supplier<World> worldSupplier)
    {

        this.worldSupplier = worldSupplier;
    }

    public void add(PLCInstruction instruction)
    {
        instructions.add(instruction);
    }

    @Override
    public PLCInstruction get(int index)
    {
        if (index < instructions.size())
        {
            return instructions.get(index);
        }
        return PLCInstruction.end();
    }

    @Override
    public int size()
    {
        return instructions.size();
    }

    @Override
    public void addBack(PLCInstruction instruction)
    {
        instructions.add(instruction);
    }

    @Override
    public void insert(int index, PLCInstruction instruction)
    {
        instructions.add(index, instruction);
    }

    @Override
    public void remove(int index)
    {
        if (index < instructions.size() && index >= 0)
            instructions.remove(index);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        for (PLCInstruction instruction : instructions)
        {
            NbtCompound instructionNbt = new NbtCompound();
            instruction.writeNbt(instructionNbt);
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

            Identifier id = Identifier.tryParse(instructionNbt.getString("id"));
            InstructionProvider provider = Instructions.REGISTRY.get(id);
            if (provider != null)
            {
                instructions.add(provider.createFromNbt(worldSupplier, instructionNbt));
            }
        }
    }
}
