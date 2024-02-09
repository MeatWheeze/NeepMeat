package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SHRInstruction implements Instruction
{
    private final int shift;

    public SHRInstruction(int shift)
    {
        this.shift = shift;
    }

    public SHRInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this.shift = nbt.getInt("shift");
    }

    @Override
    public void start(PLC plc)
    {
        var stack = plc.variableStack();
        stack.push(stack.popInt() >>> shift);
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.SHR;
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();

        if (!TokenView.isDigit(view.peek()))
            throw new NeepASM.ParseException("expected shift integer");

        int shift = view.nextInteger();

        parser.assureLineEnd(view);

        return (world, parsedSource1, program) ->
        {
            program.addBack(new SHRInstruction(shift));
        };
    }
}
