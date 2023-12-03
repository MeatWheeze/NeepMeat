package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.instruction.Argument;
import com.neep.neepmeat.api.plc.instruction.Instruction;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.program.PlcProgram;
import com.neep.neepmeat.api.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import com.neep.neepmeat.api.plc.robot.SingleAction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class MoveInstruction implements Instruction
{
    private final Supplier<World> world;
    private Argument from, to;

    private ResourceAmount<ItemVariant> stored;

    public MoveInstruction(Supplier<ServerWorld> world, List<Argument> arguments)
    {
        if (arguments.size() != 2)
        {
            throw new IllegalStateException();
        }

        this.world = world::get;
        this.from = arguments.get(0);
        this.to = arguments.get(1);
    }

    public MoveInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this.world = world;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return new NbtCompound();
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
    public void start(PlcProgram program, PLC plc)
    {
        stored = null;

        plc.addRobotAction(GroupedRobotAction.of(
                new RobotMoveToAction(plc.getRobot(), from.pos()),
                SingleAction.of(() -> takeFirst(plc)),
                new RobotMoveToAction(plc.getRobot(), to.pos()),
                SingleAction.of(() -> complete(plc))
        ), this::finish);
    }

    private void takeFirst(PLC plc)
    {
        stored = takeItem(from);
        if (stored == null)
        {
            plc.raiseError(new PLC.Error("No extractable resource found"));
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
                    plc.advanceCounter(1);
                    return;
                }
                else
                {
                    transaction.abort();
                    plc.advanceCounter(0);

                    ItemScatterer.spawn(world.get(), plc.getRobot().getX(), plc.getRobot().getY(), plc.getRobot().getZ(),
                            stored.resource().toStack((int) stored.amount()));
                }
            }
        }
    }

    private void finish(PLC plc)
    {
    }

    private ResourceAmount<ItemVariant> takeItem(Argument target)
    {
        var storage = ItemStorage.SIDED.find(world.get(), target.pos(), target.face());
        if (storage != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ResourceAmount<ItemVariant> found = StorageUtil.findExtractableContent(storage, transaction);
                if (found != null)
                {
                    long extracted = storage.extract(found.resource(), Math.min(found.amount(), 64), transaction);
                    if (extracted > 0)
                    {
                        var res = new ResourceAmount<>(found.resource(), extracted);
                        transaction.commit();
                        return res;
                    }

                    transaction.abort();
                }
            }
        }

        return null;
    }

    @Override
    public InstructionProvider getProvider()
    {
        return Instructions.MOVE;
    }
}
