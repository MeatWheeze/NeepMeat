package com.neep.neepmeat.plc.instruction;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.api.plc.PLC;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Instructions are not ticked. They make changes via {@link com.neep.neepmeat.api.plc.robot.RobotAction} which are
 * executed by the currently selected actuator in the PLC.
 */
public interface Instruction extends NbtSerialisable
{
    default boolean canStart(PLC plc) { return true; };

    void start(PLC plc);

    default void cancel(PLC plc) {};

    @Override
    default NbtCompound writeNbt(NbtCompound nbt) { return nbt; };

    @Override
    default void readNbt(NbtCompound nbt) {};

    static  <T extends Instruction> T copy(Supplier<World> worldSupplier, T t)
    {
        return (T) t.getProvider().createFromNbt(worldSupplier, t.writeNbt(new NbtCompound()));
    }

    @NotNull
    InstructionProvider getProvider();


    static Instruction end() { return EMPTY; }

    Instruction EMPTY = new Instruction()
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

        @Override
        public String toString()
        {
            return "Empty Instruction";
        }
    };

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
