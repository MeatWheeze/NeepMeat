package com.neep.neepmeat.plc.instruction;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.api.plc.PLC;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Instruction extends NbtSerialisable
{
    default boolean canStart(PLC plc) { return true; };

    void start(PLC plc);

    void cancel(PLC plc);

    @Override
    default NbtCompound writeNbt(NbtCompound nbt) { return nbt; };

    @Override
    default void readNbt(NbtCompound nbt) {};

    @NotNull
    InstructionProvider getProvider();

    Instruction EMPTY = new EmptyInstruction();

    static Instruction end() { return EMPTY; }

    class EmptyInstruction implements Instruction
    {
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
        public boolean canStart(PLC plc)
        {
            return true;
        }

        @Override
        public void start(PLC plc)
        {
            plc.setCounter(-1);
        }

        @Override
        public void cancel(PLC plc)
        {

        }

        @Override
        public @NotNull InstructionProvider getProvider()
        {
            return Instructions.END;
        }
    }

    static NbtCompound writeItem(@Nullable ResourceAmount<ItemVariant> amount)
    {
        if (amount != null)
        {
            var nbt = amount.resource().toNbt();
            nbt.putLong("amount", amount.amount());
            return nbt;
        }
        else return new NbtCompound();
    }

    @Nullable
    static ResourceAmount<ItemVariant> readItem(NbtCompound nbt)
    {
        if (nbt.contains("amount"))
        {
            long amount = nbt.getLong("amount");
            return new ResourceAmount<>(ItemVariant.fromNbt(nbt), amount);
        }
        return null;
    }
}
