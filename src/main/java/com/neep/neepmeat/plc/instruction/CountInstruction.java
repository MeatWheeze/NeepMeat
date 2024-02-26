package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CountInstruction implements Instruction
{
    private final Supplier<World> world;
    private final LazyBlockApiCache<Storage<ItemVariant>, Direction> targetCache;
    private final Argument target;
    private final ItemVariant item;

    public CountInstruction(Supplier<World> world, Argument target, ItemVariant item)
    {
        this.world = world;
        this.target = target;
        this.item = item;
        this.targetCache = LazyBlockApiCache.of(ItemStorage.SIDED, world, target);
    }

    public CountInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
    {
        this.world = worldSupplier;
        this.target = Argument.fromNbt(nbt.getCompound("target"));
        this.item = ItemVariant.fromNbt(nbt.getCompound("item"));
        this.targetCache = LazyBlockApiCache.of(ItemStorage.SIDED, world, target);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("target", target.toNbt());
        nbt.put("item", item.toNbt());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        Storage<ItemVariant> storage = targetCache.find();
        long found = 0;
        if (storage != null)
        {
            for (var view : storage)
            {
                if (matches(view.getResource()))
                {
                    found += view.getAmount();
                }
            }
        }
        plc.variableStack().push((int) found);
        plc.advanceCounter();
    }

    boolean matches(ItemVariant variant)
    {
        return item.isBlank() || item.equals(variant);
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.COUNT;
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        Argument storage = parser.parseArgument(view);
        if (storage == null)
            throw new NeepASM.ParseException("expected storage world target");

        view.fastForward();
        String name = view.nextString();
        ItemVariant variant;
        if (!name.isEmpty())
        {
            Item item = Registries.ITEM.getOrEmpty(Identifier.tryParse(name)).orElse(null);
            if (item == null)
                throw new NeepASM.ParseException("item '" + name + "' not known");

            variant = ItemVariant.of(item);

        }
        else
        {
            variant = ItemVariant.blank();
        }

        parser.assureLineEnd(view);

        return (world, parsedSource1, program) ->
        {
            program.addBack(new CountInstruction(() -> world, storage, variant));
        };
    }
}
