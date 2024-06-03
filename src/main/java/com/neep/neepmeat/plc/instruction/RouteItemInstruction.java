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
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class RouteItemInstruction implements Instruction
{
    private final Argument from;
    private final Argument to;
    private final Pattern pattern;

    public RouteItemInstruction(Supplier<World> worldSupplier, Argument from, Argument to, String pattern)
    {
        this.pattern = Pattern.compile(pattern);
        this.from = from;
        this.to = to;
    }

    public RouteItemInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this(world,
                Argument.fromNbt(nbt.getCompound("from")),
                Argument.fromNbt(nbt.getCompound("to")),
                nbt.getString("pattern")
        );
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putString("pattern", pattern.pattern());
        nbt.put("from", from.toNbt());
        nbt.put("to", to.toNbt());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(AtomicAction.of(p ->
        {
            var stack = p.variableStack();

            int amount;
            if (stack.isEmpty())
                amount = 1;
            else
                amount = p.variableStack().popInt();

            if (plc.getActuator() instanceof PipeDriverBlock.PipeDriverBlockEntity be)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    Predicate<ItemVariant> predicate = v ->
                    {
                        String id = v.getItem().getRegistryEntry().registryKey().getValue().toString();
                        return pattern.asMatchPredicate().test(id);
                    };

//                    ResourceAmount<ItemVariant> ra = new ResourceAmount<>(item, amount);
                    boolean satisfied = be.getNetwork(null).route(predicate, amount, from.pos(), from.face(), to.pos(), to.face(), RoutingNetwork.RequestType.EXACT_AMOUNT, transaction);

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
        return Instructions.ROUTE;
    }

    public static boolean isSimplePattern(String identifier)
    {
        return identifier.matches("^[a-zA-Z0-9_:]+$");
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        Argument from = parser.parseArgument(view);
        if (from == null)
            throw new NeepASM.ParseException("expected input pipe world target");

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
            Item item = Registries.ITEM.getOrEmpty(Identifier.tryParse(notGlob)).orElse(null);
            if (item == null)
                throw new NeepASM.ParseException("item '" + notGlob + "' not known");
        }
        String regex = convertToRegex(notGlob);

        parser.assureLineEnd(view);

        return (world, parsedSource1, program) ->
        {
            program.addBack(new RouteItemInstruction(() -> world, from, to, regex));
        };
    }

    private static String convertToRegex(String notGlob)
    {
        return notGlob.replace("*", ".*");
    }
}
