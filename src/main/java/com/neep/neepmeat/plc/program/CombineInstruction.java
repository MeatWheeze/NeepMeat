package com.neep.neepmeat.plc.program;

import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.PLC;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.component.TableComponent;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.recipe.CombineStep;
import com.neep.neepmeat.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.plc.robot.RobotAction;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.ItemStack;
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
    private final Supplier<World> worldSupplier;
    protected Argument from;
    protected Argument to;
    private ResourceAmount<ItemVariant> stored;

    public CombineInstruction(Supplier<ServerWorld> worldSupplier, List<Argument> arguments)
    {
        this.worldSupplier = () -> (ServerWorld) worldSupplier.get();
        this.from = arguments.get(0);
        this.to = arguments.get(1);
    }

    public CombineInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
    {
        this.worldSupplier = worldSupplier;
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

    private void thingy(PLC plc)
    {
        var fromStructure = TableComponent.LOOKUP.find(worldSupplier.get(), from.pos(), null);
        var toStructure = TableComponent.LOOKUP.find(worldSupplier.get(), to.pos(), null);

        if (fromStructure != null && toStructure != null)
        {
//            fromStructure.getStorage().
        }

        plc.raiseError(new PLC.Error(Text.of("Invalid structure")));
    }

    private void takeFirst(PLC plc)
    {
        System.out.println("Take first");
        this.stored = receiveItem(LazyBlockApiCache.itemSided(from, () -> (ServerWorld) worldSupplier.get()));
        if (stored == null)
        {
            plc.raiseError(new PLC.Error(Text.of("Oh noes!")));
        }
    }

    private void complete(PLC plc)
    {
        var mip = MutateInPlace.ITEM.find(worldSupplier.get(), to.pos(), null);
        if (mip != null)
        {
            ItemStack stack = mip.get();

            var step = new CombineStep(stored.resource().toStack((int) stored.amount()));

            NMComponents.WORKPIECE.maybeGet(stack).ifPresent(workpiece ->
            {
                workpiece.addStep(step);
                System.out.println("Combining");
            });

            mip.set(stack);
        }
    }

    private ResourceAmount<ItemVariant> receiveItem(LazyBlockApiCache<Storage<ItemVariant>, Direction> target)
    {
        var storage = target.find();
        if (storage != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ResourceAmount<ItemVariant> found = StorageUtil.findExtractableContent(storage, transaction);
                if (found != null)
                {
                    long extracted = storage.extract(found.resource(), found.amount(), transaction);
                    if (extracted > 0)
                    {
                        var res = new ResourceAmount<>(found.resource(), extracted);
                        transaction.commit();
                        return res;
                    }
                    transaction.abort();
                    return null;
                }
            }
        }
        else
        {
            return null;
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