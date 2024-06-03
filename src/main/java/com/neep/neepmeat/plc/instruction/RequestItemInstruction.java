package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.AtomicAction;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.block.item_transport.PipeDriverBlock;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static com.neep.neepmeat.neepasm.compiler.Parser.convertToRegex;
import static com.neep.neepmeat.neepasm.compiler.Parser.isSimplePattern;

public class RequestItemInstruction implements Instruction
{
//    private final LazyBlockApiCache<Void, Void> routerCache;
    private final Argument to;
    private final Pattern pattern;

    public RequestItemInstruction(Supplier<World> worldSupplier, Argument to, String pattern)
    {
//        routerCache = LazyBlockApiCache.of(MeatLib.VOID_LOOKUP, router.pos(), worldSupplier, () -> null);
        this.pattern = Pattern.compile(pattern);
        this.to = to;
    }

    public RequestItemInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this(world, Argument.fromNbt(nbt.getCompound("to")), nbt.getString("pattern"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putString("pattern", pattern.pattern());
        nbt.put("to", to.toNbt());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(AtomicAction.of(p ->
        {
            var stack = p.variableStack();

            int amount = 0;
            if (stack.isEmpty())
                amount = 1;
            else
                amount = p.variableStack().popInt();

            if (plc.getActuator() instanceof PipeDriverBlock.PipeDriverBlockEntity be)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    Predicate<ItemVariant> predicate = v ->
                            pattern.asMatchPredicate().test(v.getItem().getRegistryEntry().registryKey().getValue().toString());
                    boolean satisfied = be.getNetwork(null).request(predicate, amount, to.pos(), to.face(), RoutingNetwork.RequestType.EXACT_AMOUNT, transaction);

                    if (satisfied)
                        transaction.commit();

                    p.variableStack().push(satisfied ? 1 : 0);
                }
            }
            else
            {
                plc.raiseError(new PLC.Error("Actuator is not a pipe driver"));
            }
        }), PLC::advanceCounter);
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.REQUEST;
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        Argument to = parser.parseArgument(view);
        if (to == null)
            throw new NeepASM.ParseException("expected output pipe world target");

        String notGlob = view.nextString();
        if (notGlob == null)
            throw new NeepASM.ParseException("expected item ID string (minecraft:stone)");

        view.fastForward();

        // If a normal string is given, verify it against all items in the registry.
        if (isSimplePattern(notGlob))
        {
            Item item = Registry.ITEM.getOrEmpty(Identifier.tryParse(notGlob)).orElse(null);
            if (item == null)
                throw new NeepASM.ParseException("item '" + notGlob + "' not known");
        }
        String regex = convertToRegex(notGlob);

        parser.assureLineEnd(view);

        return (world, parsedSource1, program) ->
        {
            program.addBack(new RequestItemInstruction(() -> world, to, regex));
        };
    }
}
