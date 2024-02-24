package com.neep.neepmeat.plc.instruction;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.AtomicAction;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.block.item_transport.PipeDriverBlock;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class RequestItemInstruction implements Instruction
{
//    private final LazyBlockApiCache<Void, Void> routerCache;
    private final Argument to;
    private final ItemVariant item;

    public RequestItemInstruction(Supplier<World> worldSupplier, Argument to, ItemVariant item)
    {
//        routerCache = LazyBlockApiCache.of(MeatLib.VOID_LOOKUP, router.pos(), worldSupplier, () -> null);
        this.item = item;
        this.to = to;
    }

    public RequestItemInstruction(Supplier<World> world, NbtCompound nbt)
    {
//        routerCache = LazyBlockApiCache.of(MeatLib.VOID_LOOKUP, NbtHelper.toBlockPos(nbt.getCompound("router")), world, () -> null);
        this.item = ItemVariant.fromNbt(nbt.getCompound("item"));
        this.to = Argument.fromNbt(nbt.getCompound("to"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
//        nbt.put("router", NbtHelper.fromBlockPos(routerCache.pos()));
        nbt.put("item", item.toNbt());
        nbt.put("to", to.toNbt());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
//        if (!plc.getActuator().capabilities().contains(PLCActuator.Capability.ROUTE_ITEM))
//            plc.raiseError(new PLC.Error("Acutator does not have routing capability"));

        plc.addRobotAction(AtomicAction.of(p ->
        {
            int amount = p.variableStack().popInt();
            if (plc.getActuator() instanceof PipeDriverBlock.PipeDriverBlockEntity be)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    ResourceAmount<ItemVariant> ra = new ResourceAmount<>(item, amount);
                    be.getNetwork(null).request(ra, to.pos(), to.face(), RoutingNetwork.RequestType.EXACT_AMOUNT, transaction);
                    transaction.commit();
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
//        view.fastForward();
//        Argument router = parser.parseArgument(view);
//        if (router == null)
//            throw new NeepASM.ParseException("expected router world target");

        view.fastForward();
        Argument to = parser.parseArgument(view);
        if (to == null)
            throw new NeepASM.ParseException("expected output pipe world target");

        String string = view.nextString();
        if (string == null)
            throw new NeepASM.ParseException("expected item ID string (minecraft:stone)");

        view.fastForward();

        Item item = Registry.ITEM.getOrEmpty(Identifier.tryParse(string)).orElse(null);
        if (item == null)
            throw new NeepASM.ParseException("item '" + string + "' not known");
        ItemVariant itemVariant = ItemVariant.of(item);

        view.fastForward();

        return (world, parsedSource1, program) ->
        {
            program.addBack(new RequestItemInstruction(() -> world, to, itemVariant));
        };
    }
}
