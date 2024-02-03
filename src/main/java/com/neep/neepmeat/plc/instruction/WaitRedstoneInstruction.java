package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.RedstoneInterface;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class WaitRedstoneInstruction implements Instruction
{
    private final WaitAction action = new WaitAction();
    private final Supplier<World> worldSupplier;
    private final LazyBlockApiCache<RedstoneInterface, Direction> redstoneCache;

    private final Argument target;

    public WaitRedstoneInstruction(Supplier<World> worldSupplier, List<Argument> arguments)
    {
        this.target = arguments.get(0);
        this.worldSupplier = worldSupplier;
        this.redstoneCache = LazyBlockApiCache.of(RedstoneInterface.LOOKUP, () -> (ServerWorld) worldSupplier.get(), target);
    }

    public WaitRedstoneInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
    {
        this.target = Argument.fromNbt(nbt.getCompound("target"));
        this.worldSupplier = worldSupplier;
        this.redstoneCache = LazyBlockApiCache.of(RedstoneInterface.LOOKUP, () -> (ServerWorld) worldSupplier.get(), target);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("target", target.toNbt());
        return nbt;
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
    public void start(PLC plc)
    {
        plc.addRobotAction(action, this::finish);
    }

    @Override
    public void cancel(PLC plc)
    {

    }

    private void finish(PLC plc)
    {
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.WAIT_REDSTONE;
    }

    class WaitAction implements RobotAction
    {
        @Override
        public boolean finished(PLC plc)
        {
            if (worldSupplier.get().getTime() % 2 == 0)
            {
                RedstoneInterface redstone = redstoneCache.find();
                if (redstone != null)
                {
                    return redstone.getReceivedStrength() > 0;
                }
            }
            return false;
        }

        @Override
        public void start(PLC plc)
        {

        }

        @Override
        public void tick(PLC plc)
        {
        }

        @Override
        public void end(PLC plc)
        {

        }
    }
}
