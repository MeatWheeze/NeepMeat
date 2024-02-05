package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.InstructionParser;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class RobotInstruction implements Instruction
{
    private final @Nullable Argument target;

    public RobotInstruction(@Nullable Argument target)
    {
        this.target = target;
    }

    public RobotInstruction(Supplier<World> world, NbtCompound nbt)
    {
        if (nbt.contains("target"))
            this.target = Argument.fromNbt(nbt.getCompound("target"));
        else
            this.target = null;
    }

    public RobotInstruction(Supplier<World> worldSupplier, List<Argument> arguments)
    {
        this.target = arguments.get(0);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        if (target != null)
            nbt.put("target", target.toNbt());
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        if (target != null)
            plc.selectActuator(target.pos());
        else
            plc.selectActuator(null);

        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.ROBOT;
    }

    public static class Parser implements InstructionParser
    {
        @Override
        public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, com.neep.neepmeat.neepasm.compiler.Parser parser) throws NeepASM.ParseException
        {
            view.fastForward();
            Argument target = parser.parseArgument(view);
            return ((world, source, program) ->
            {
                program.addBack(new RobotInstruction(target));
            });
        }
    }
}
