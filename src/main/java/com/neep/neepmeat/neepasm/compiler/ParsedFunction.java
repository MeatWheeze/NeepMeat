package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.instruction.CallInstruction;
import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.instruction.ReturnInstruction;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class ParsedFunction
{
    private final String name;
    private final List<ObjectIntPair<ParsedInstruction>> instructions = Lists.newArrayList();
    private final List<Label> labels = Lists.newArrayList();

    public ParsedFunction(String name)
    {
        this.name = name;
    }

    public void instruction(ParsedInstruction instruction, int line)
    {
        instructions.add(ObjectIntPair.of(instruction, line));
    }

    public void label(Label label)
    {
        labels.add(label);
    }

    public int size()
    {
        return instructions.size();
    }

    public String mangledName()
    {
        return "function#" + name;
    }

    public String name()
    {
        return name;
    }

    // Executed during compilation
    public void call(ServerWorld world, ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException
    {
        Label label = parsedSource.findLabel(mangledName());
        if (label == null)
            throw new NeepASM.CompilationException(name + ": label '" + mangledName() + "' does not exist");

        program.addBack(new CallInstruction(label));
    }

    // Executed after parsing
    public void expand(ParsedSource parsedSource)
    {
        parsedSource.label(new Label(mangledName(), parsedSource.size()));
        for (Label label : labels)
        {
            parsedSource.label(new Label(label.name(), parsedSource.size() + label.index()));
        }

        int line = -1;
        for (var pair : instructions)
        {
            parsedSource.instruction(pair.key(), pair.valueInt());
            line = pair.valueInt();
        }
        parsedSource.instruction(((world, source, program) -> program.addBack(new ReturnInstruction())), line);
    }

//    public void build(ServerWorld serverWorld, ParsedSource parsedSource, MutableProgram program) throws NeepASM.CompilationException
//    {
////        program.addLabel(new Label("function#" + name, program.size()));
////        for (Label label : labels)
////        {
////            program.addLabel(new Label(label.name(), program.size() + label.index()));
////        }
////
////        for (ParsedInstruction preInstruction : instructions)
////        {
////            preInstruction.build(serverWorld, parsedSource, program);
////        }
////        program.addBack(new ReturnInstruction());
//    }
}
