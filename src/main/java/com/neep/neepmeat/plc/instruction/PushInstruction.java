package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.InstructionParser;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.compiler.variable.IntVariable;
import com.neep.neepmeat.neepasm.compiler.variable.Variable;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PushInstruction implements Instruction
{
    private final int immediate;
//    private final Variable<?> immediate;

    public PushInstruction(int immediate)
    {
        this.immediate = immediate;
    }

    public PushInstruction(Supplier<World> world, NbtCompound nbt)
    {
        immediate = nbt.getInt("immediate");
    }

    @Override
    public void start(PLC plc)
    {
        plc.variableStack().push(immediate);
        plc.advanceCounter();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt("immediate", immediate);
        return nbt;
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.PUSH;
    }

    public static class Parser implements InstructionParser
    {
        @Override
        public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, com.neep.neepmeat.neepasm.compiler.Parser parser) throws NeepASM.ParseException
        {
            Variable<?> variable = parseImmediate(view);

            parser.assureLineEnd(view);

            return (((world, source, program) ->
                    program.addBack(new PushInstruction((Integer) variable.value()))));
        }

        private static Variable<?> parseImmediate(TokenView view) throws NeepASM.ParseException
        {
            view.fastForward();
            char c = view.peek();

//            if (c == '"')
//            {
//                String string = view.nextString();
//                if (string.isEmpty())
//                    throw new NeepASM.ParseException("invalid string");
//
//                return new StringVariable(string);
//            }
            if (TokenView.isDigit(c))
            {
                int i = view.nextInteger();
                return new IntVariable(i);
            }

            throw new NeepASM.ParseException("invalid immediate variable");
        }
    }
}
