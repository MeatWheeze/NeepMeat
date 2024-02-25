package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.AtomicAction;
import com.neep.neepmeat.api.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.api.plc.robot.SoundAction;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class MoveInstruction implements Instruction
{
    private final Supplier<World> world;
    private final GroupedRobotAction group;
    private final Argument from;
    private final Argument to;
    private final int amount;

    private ResourceAmount<ItemVariant> stored;

    public MoveInstruction(Supplier<World> world, List<Argument> arguments)
    {
        this(world, arguments.get(0), arguments.get(1), 64);
    }

    public MoveInstruction(Supplier<World> world, Argument from, Argument to, int count)
    {
        this.world = world;
        this.from = from;
        this.to = to;
        this.amount = count;

        group = GroupedRobotAction.of(
                new RobotMoveToAction(from.pos()),
                AtomicAction.of(this::takeFirst),
                new SoundAction(world, SoundEvents.BLOCK_BEEHIVE_EXIT),
                new RobotMoveToAction(to.pos()),
                AtomicAction.of(this::complete),
                new SoundAction(world, SoundEvents.BLOCK_BEEHIVE_ENTER)
        );
    }

    public MoveInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this(world,
                Argument.fromNbt(nbt.getCompound("from")),
                Argument.fromNbt(nbt.getCompound("to")),
                nbt.getInt("amount")
            );
        group.readNbt(nbt.getCompound("action"));
        this.stored = Instruction.readItem(nbt.getCompound("stored"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("from", from.toNbt());
        nbt.put("to", to.toNbt());
        nbt.put("action", group.writeNbt(new NbtCompound()));
        nbt.put("stored", Instruction.writeItem(stored));
        nbt.putInt("amount", amount);
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(group, this::finish);
    }

    @Override
    public void cancel(PLC plc)
    {
        plc.getActuator().spawnItem(stored);
        group.end(plc);
        stored = null;
    }

    private void takeFirst(PLC plc)
    {
        stored = Instructions.takeItem(from, world, 64);
        if (stored == null)
        {
//            plc.raiseError(new PLC.Error("No extractable resource found at " + from.pos() + ", " + from.face()));
            cancel(plc);
            finish(plc);
        }
    }

    private void complete(PLC plc)
    {
        var storage = ItemStorage.SIDED.find(world.get(), to.pos(), to.face());
        if (stored != null && storage != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                long inserted = storage.insert(stored.resource(), stored.amount(), transaction);
                if (inserted == stored.amount())
                {
                    transaction.commit();
                    stored = null;
                    return;
                }
                else
                {
                    transaction.abort();
                }
            }
        }

        plc.getActuator().spawnItem(stored);
        stored = null;
    }

    private void finish(PLC plc)
    {
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.MOVE;
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        Argument from = parser.parseArgument(view);
        if (from == null)
            throw new NeepASM.ParseException("expected source world target");

        view.fastForward();
        Argument to = parser.parseArgument(view);
        if (to == null)
            throw new NeepASM.ParseException("expected destination world target");

        view.fastForward();

        int count;
        if (TokenView.isDigit(view.peek()))
            count = view.nextInteger();
        else
            count = 64;

        parser.assureLineEnd(view);
        return (world, parsedSource1, program) ->
        {
            program.addBack(new MoveInstruction(() -> world, from, to, count));
        };
    }
}
