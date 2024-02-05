package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.RedstoneInterface;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class WaitRedstoneInstruction implements Instruction
{
    private final WaitAction action = new WaitAction();
    private final Supplier<World> worldSupplier;
    private final LazyBlockApiCache<RedstoneInterface, Direction> redstoneCache;

    private final Argument target;

    public WaitRedstoneInstruction(Supplier<World> worldSupplier, Argument target)
    {
        this.target = target;
        this.worldSupplier = worldSupplier;
        this.redstoneCache = LazyBlockApiCache.of(RedstoneInterface.LOOKUP, worldSupplier, this.target);
    }

    public WaitRedstoneInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
    {
        this.target = Argument.fromNbt(nbt.getCompound("target"));
        this.worldSupplier = worldSupplier;
        this.redstoneCache = LazyBlockApiCache.of(RedstoneInterface.LOOKUP, worldSupplier, target);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("target", target.toNbt());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(action, this::finish);
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

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        Argument target = parser.parseArgument(view);
        if (target == null)
            throw new NeepASM.ParseException("expected redstone target");

        return ((world, source, program) ->
                program.addBack(new WaitRedstoneInstruction(() -> world, target)));
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
