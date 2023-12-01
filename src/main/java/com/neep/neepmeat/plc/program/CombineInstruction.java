package com.neep.neepmeat.plc.program;

import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.PLC;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.ImmediateInstruction;
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
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
//        var fromStorage = from.find();
//        var toStorage = to.find();
//        if (fromStorage == null || toStorage == null)
//            return false;

//        long extracted = fromStorage.extract()

        return true;
    }

    @Override
    public void start(PlcProgram program, PLC plc)
    {
        plc.addRobotAction(GroupedRobotAction.of(
                new RobotMoveToAction(plc.getRobot(), from.pos()),
                new RobotMoveToAction(plc.getRobot(), to.pos())
        ), this::finish);
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

    public static class Immediate implements ImmediateInstruction
    {
        @Nullable private ResourceAmount<ItemVariant> storedResource;
        private final World world;
        private int arguments = 0;

        public Immediate(World world)
        {
            this.world = world;
        }

        @Override
        public void argument(Argument argument, PLC plc)
        {
            if (arguments == 0)
            {
                arguments = 1;
                plc.addRobotAction(SingleAction.of(
                    () ->
                    {
                        this.storedResource = receiveItem(plc, LazyBlockApiCache.itemSided(argument, () -> (ServerWorld) world));
                    }),
                    this::onTakeItem);
            }
            else if (arguments == 1)
            {
                arguments = 2;
                plc.addRobotAction(SingleAction.of(
                    () ->
                    {
                        combineItems(plc, LazyBlockApiCache.itemSided(argument, () -> (ServerWorld) world));
                    }),
                    this::onTakeItem);
            }
        }

        @Override
        public void interrupt(PLC plc)
        {

        }

        @Override
        public boolean isFinished()
        {
            return arguments == 2;
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
                            var res =  new ResourceAmount<>(found.resource(), extracted);
                            transaction.commit();
                            return res;
                        }
                    }
                }
                else
                {
                    plc.raiseError();
                }
            }
            return new ResourceAmount<>(ItemVariant.blank(), 0);
        }

        private void combineItems(PLC plc, LazyBlockApiCache<Storage<ItemVariant>, Direction> target)
        {
            var first = storedResource;
            var second = receiveItem(plc, target);

            ItemScatterer.spawn(world, target.pos().getX() + 0.5, target.pos().getY() + 0.5, target.pos().getZ() + 0.5, first.resource().toStack((int) first.amount()));
            ItemScatterer.spawn(world, target.pos().getX() + 0.5, target.pos().getY() + 0.5, target.pos().getZ() + 0.5, second.resource().toStack((int) second.amount()));
        }

        private void onTakeItem(PLC plc)
        {

        }

        interface SingleAction extends RobotAction
        {
            static SingleAction of(Runnable action)
            {
                return action::run;
            }

            @Override
            default boolean finished() { return true; }

            @Override
            default void tick() {}
        }

        class TakeItemAction implements RobotAction
        {
            private final LazyBlockApiCache<Storage<ItemVariant>, Direction> target;
            private final PLC plc;

            public TakeItemAction(LazyBlockApiCache<Storage<ItemVariant>, Direction> target, PLC plc)
            {
                this.target = target;
                this.plc = plc;
            }

            @Override
            public boolean finished()
            {
                return true;
            }

            @Override
            public void start()
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    var storage = target.find();
                    if (storage != null)
                    {
                        var found = StorageUtil.findExtractableContent(storage, transaction);
                        if (found != null)
                        {
                            long extracted = storage.extract(found.resource(), found.amount(), transaction);
                            if (extracted > 0)
                            {
//                                receiveItem(new ResourceAmount<>(found.resource(), extracted), transaction);
                                transaction.commit();
                            }
                        }
                    }
                    else
                    {
                        plc.raiseError();
                    }
                }
            }

            @Override
            public void tick()
            {

            }
        }
    }
}
