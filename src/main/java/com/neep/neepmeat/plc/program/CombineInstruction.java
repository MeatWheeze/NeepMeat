package com.neep.neepmeat.plc.program;

import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.PLC;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.plc.robot.RobotAction;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class CombineInstruction implements Instruction
{
    protected LazyBlockApiCache<Storage<ItemVariant>, Direction> from;
    protected LazyBlockApiCache<Storage<ItemVariant>, Direction> to;

    public CombineInstruction(Supplier<ServerWorld> worldSupplier, List<Argument> arguments)
    {
        Argument from = arguments.get(0);
        Argument to = arguments.get(1);
        this.from = LazyBlockApiCache.of(ItemStorage.SIDED, from.pos(), worldSupplier, from::face);
        this.to = LazyBlockApiCache.of(ItemStorage.SIDED, to.pos(), worldSupplier, to::face);
    }

    public CombineInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
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
        plc.addRobotAction(GroupedRobotAction.of(
                new RobotMoveToAction(plc.getRobot(), from.pos()),
                SingleAction.of(this::takeFirst),
                new RobotMoveToAction(plc.getRobot(), to.pos()),
                SingleAction.of(this::complete)
        ), this::finish);
    }

    private void takeFirst()
    {
        System.out.println("Take first");
    }

    private void complete()
    {
        System.out.println("Take second");
    }

    private ResourceAmount<ItemVariant> receiveItem(PLC plc, LazyBlockApiCache<Storage<ItemVariant>, Direction> target)
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            var storage = target.find();
            if (storage != null)
            {
                ResourceAmount<ItemVariant> found = StorageUtil.findExtractableContent(storage, transaction);
                if (found != null)
                {
                    long extracted = storage.extract(found.resource(), found.amount(), transaction);
                    if (extracted > 0)
                    {
//                            this.storedResource = new ResourceAmount<>(found.resource(), extracted);
                        var res = new ResourceAmount<>(found.resource(), extracted);
                        transaction.commit();
                        return res;
                    }
                }
            }
            else
            {
                plc.raiseError(new PLC.Error(Text.of("Oh noes!")));
            }
        }
        return new ResourceAmount<>(ItemVariant.blank(), 0);
    }

    @Override
    public InstructionProvider getProvider()
    {
        return Instructions.COMBINE;
    }

    void finish(PLC plc)
    {
        plc.advanceCounter();
        System.out.println("Finish");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    // Here's the question: createFromNbt or readNbt
    @Override
    public void readNbt(NbtCompound nbt)
    {

    }

    interface SingleAction extends RobotAction
    {
        static SingleAction of(Runnable action)
        {
            return action::run;
        }

        @Override
        default boolean finished()
        {
            return true;
        }

        @Override
        default void tick()
        {
        }
    }
}