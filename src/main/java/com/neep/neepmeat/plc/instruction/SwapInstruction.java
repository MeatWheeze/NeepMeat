package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SwapInstruction implements Instruction
{
    public SwapInstruction()
    {

    }

    public SwapInstruction(Supplier<World> world, NbtCompound nbt)
    {
    }

    @Override
    public void start(PLC plc)
    {
        var stack = plc.variableStack();
        int first = stack.popInt();
        int second = stack.popInt();
        stack.push(second);
        stack.push(first);
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.SWAP;
    }

//    public static class Parser implements InstructionParser
//    {
//        @Override
//        public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, com.neep.neepmeat.neepasm.compiler.Parser parser) throws NeepASM.ParseException
//        {
//            view.fastForward();
//            char c = view.peek();
//
//            Variable<?> variable;
//            if (c == '"')
//            {
//                String string = view.nextString();
//                if (string.isEmpty())
//                    throw new NeepASM.ParseException("invalid string");
//
//                variable = new StringVariable(string);
//
//                return ((world, source, program) ->
//                {
//                    program.addBack(new PopInstruction(variable));
//                });
//            }
//            return null;
//        }
//    }
}
