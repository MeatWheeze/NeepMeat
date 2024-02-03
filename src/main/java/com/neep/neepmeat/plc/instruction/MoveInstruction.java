package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.AtomicAction;
import com.neep.neepmeat.api.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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

    private ResourceAmount<ItemVariant> stored;

    public MoveInstruction(Supplier<World> world, List<Argument> arguments)
    {
        if (arguments.size() != 2)
        {
            throw new IllegalStateException();
        }

        this.world = world;
        this.from = arguments.get(0);
        this.to = arguments.get(1);

        group = GroupedRobotAction.of(
                new RobotMoveToAction(from.pos()),
                AtomicAction.of(this::takeFirst),
                new RobotMoveToAction(to.pos()),
                AtomicAction.of(this::complete)
        );
    }

    public MoveInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this(world, List.of(
                Argument.fromNbt(nbt.getCompound("from")),
                Argument.fromNbt(nbt.getCompound("to"))
            ));
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
        return new NbtCompound();
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(group, this::finish);
    }

    @Override
    public void cancel(PLC plc)
    {
        plc.getRobot().spawnItem(stored);
        group.end(plc);
        stored = null;
    }

    private void takeFirst(PLC plc)
    {
        stored = Instructions.takeItem(from, world, 64);
        if (stored == null)
        {
//            plc.raiseError(new PLC.Error("No extractable resource found at " + from.pos() + ", " + from.face()));
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

        plc.getRobot().spawnItem(stored);
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
}
