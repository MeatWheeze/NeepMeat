package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.neep.neepmeat.api.plc.instruction.PredicatedInstructionBuilder;
import com.neep.neepmeat.api.plc.instruction.SimpleInstructionProvider;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.PreInstruction;
import com.neep.neepmeat.neepasm.program.KeyValue;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.neepasm.program.MutableProgram;
import com.neep.neepmeat.plc.ArgumentPredicates;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.instruction.MoveInstruction;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class Compiler
{
//    private final Map<String, InstructionProvider> instructionMap = Maps.newHashMap();
//
//    public static final InstructionProvider MOVE = new SimpleInstructionProvider(MoveInstruction::new, MoveInstruction::new, 2, Text.of("MOVE"))
//            .factory(PredicatedInstructionBuilder.create()
//                    .arg(ArgumentPredicates.IS_ITEM_STORAGE)
//                    .arg(ArgumentPredicates.IS_ITEM_STORAGE));

    public Compiler()
    {
//        Instructions.REGISTRY.forEach(provider ->
//        {
//            instructionMap.put(provider.getParseName(), provider);
//        });

//        instructionMap.put(MOVE.getParseName(), MOVE);
    }

    public void compile(ParsedSource parsedSource)
    {
        MutableProgram program = new MutableProgram();
        try
        {
            for (PreInstruction preInstruction : parsedSource.instructions())
            {
                program.put(preInstruction.build(parsedSource, program));
            }
        }
        catch (NeepASM.CompilationException e)
        {
            e.printStackTrace();
        }
    }

}
