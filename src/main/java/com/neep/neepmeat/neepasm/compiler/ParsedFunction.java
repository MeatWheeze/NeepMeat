package com.neep.neepmeat.neepasm.compiler;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.instruction.CallInstruction;
import com.neep.neepmeat.api.plc.program.MutableProgram;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedMacro;
import com.neep.neepmeat.neepasm.program.Label;
import com.neep.neepmeat.plc.instruction.ReturnInstruction;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class ParsedFunction implements InstructionAcceptor
{
    private final String name;
    private final Function<String, ParsedMacro> macroFinder;

    private final List<ObjectIntPair<ParsedInstruction>> instructions = Lists.newArrayList();
    private final List<Label> labels = Lists.newArrayList();

    /**
     * @param name Function name
     * @param macroFinder A link to the main program's list of macros
     */
    public ParsedFunction(String name, Function<String, ParsedMacro> macroFinder)
    {
        this.name = name;
        this.macroFinder = macroFinder;
    }

    @Override
    public void instruction(ParsedInstruction instruction, int line)
    {
        instructions.add(ObjectIntPair.of(instruction, line));
    }

    @Override
    public void label(Label label)
    {
        labels.add(label);
    }

    @Override
    public int size()
    {
        return instructions.size();
    }

    @Override
    public @Nullable ParsedMacro findMacro(String name)
    {
        return macroFinder.apply(name);
    }

    public String mangledName()
    {
        return "function#" + name;
    }

    public Label mangleLabel(String label, int index)
    {
        return new Label(ParsedSource.mangleLabel(label, name), index);
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
}
