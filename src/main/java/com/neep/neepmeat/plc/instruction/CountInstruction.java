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
import java.util.regex.Pattern;

import static com.neep.neepmeat.neepasm.compiler.Parser.convertToRegex;
import static com.neep.neepmeat.neepasm.compiler.Parser.isSimplePattern;

public class CountInstruction implements Instruction
{
    private final Supplier<World> world;
    private final LazyBlockApiCache<Storage<ItemVariant>, Direction> targetCache;
    private final Argument target;
    private final Pattern pattern;

    public CountInstruction(Supplier<World> world, Argument target, String pattern)
    {
        this.world = world;
        this.target = target;
        this.pattern = Pattern.compile(pattern);
        this.targetCache = LazyBlockApiCache.of(ItemStorage.SIDED, world, target);
    }

    public CountInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
    {
        this(worldSupplier, Argument.fromNbt(nbt.getCompound("target")), nbt.getString("pattern"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("target", target.toNbt());
        nbt.putString("pattern", pattern.pattern());
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

    private boolean matches(ItemVariant resource)
    {
        return pattern.asMatchPredicate().test(resource.getItem().getRegistryEntry().registryKey().getValue().toString());
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

        String regex;
        String notGlob = view.nextString();
        if (notGlob != null)
        {
            if (isSimplePattern(notGlob))
            {
                Item item = Registry.ITEM.getOrEmpty(Identifier.tryParse(notGlob)).orElse(null);
                if (item == null)
                    throw new NeepASM.ParseException("item '" + notGlob + "' not known");
            }
            regex = convertToRegex(notGlob);
        }
        else
        {
            char c = view.peek();
            parser.assureLineEnd(view);
            regex = ".*";
        }

        parser.assureLineEnd(view);

        return (world, parsedSource1, program) ->
        {
            program.addBack(new CountInstruction(() -> world, storage, regex));
        };
    }
}
