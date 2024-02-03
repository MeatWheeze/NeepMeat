package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.InstructionParser;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DelayInstruction implements Instruction
{
    private final int time;
    private final DelayAction action = new DelayAction();

    public DelayInstruction(int time)
    {
        this.time = time;
        action.counter = time;
    }

    public DelayInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this.time = nbt.getInt("time");
        this.action.counter = nbt.getInt("counter");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt("time", time);
        nbt.putInt("counter", action.counter);
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(action, this::finish);
    }

    private void finish(PLC plc)
    {
        action.counter = time;
        plc.advanceCounter();
    }

    @Override
    public void cancel(PLC plc)
    {
        action.counter = time;
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.DELAY;
    }

    private class DelayAction implements RobotAction
    {
        private int counter;

        @Override
        public boolean finished(PLC plc)
        {
            return counter == 0;
        }

        @Override
        public void start(PLC plc)
        {

        }

        @Override
        public void tick(PLC plc)
        {
            counter = Math.max(0, counter - 1);
        }

        @Override
        public void end(PLC plc)
        {
            counter = time;
        }
    }

    public static class Parse implements InstructionParser
    {
        @Override
        public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
        {
            view.fastForward();
            if (!TokenView.isDigit(view.peek()))
                throw new NeepASM.ParseException("expected delay in ticks");

            int delay = view.nextInteger();

            return ((world, source, program) ->
                    program.addBack(new DelayInstruction(delay)));
        }
    }
}
