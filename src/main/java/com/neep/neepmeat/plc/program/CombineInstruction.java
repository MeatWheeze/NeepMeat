package com.neep.neepmeat.plc.program;

import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.PLC;
import com.neep.neepmeat.plc.instruction.*;
import com.neep.neepmeat.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.plc.robot.RobotAction;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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
        @Override
        public void argument(Argument argument, PLC plc)
        {
        }

        @Override
        public void interrupt(PLC plc)
        {

        }

        class TakeItemAction implements RobotAction
        {
            public TakeItemAction()
            {

            }

            @Override
            public boolean finished()
            {
                return true;
            }

            @Override
            public void start()
            {

            }

            @Override
            public void tick()
            {

            }
        }
    }
}
