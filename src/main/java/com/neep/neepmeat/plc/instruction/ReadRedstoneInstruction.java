package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.RedstoneInterface;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ReadRedstoneInstruction implements Instruction
{
    private final Argument target;
    private final LazyBlockApiCache<RedstoneInterface, Direction> redstoneCache;

    public ReadRedstoneInstruction(Supplier<World> world, Argument argument)
    {
        this.target = argument;
        this.redstoneCache = LazyBlockApiCache.of(RedstoneInterface.LOOKUP, world, target);
    }

    public ReadRedstoneInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this.target = Argument.fromNbt(nbt.getCompound("target"));
        this.redstoneCache = LazyBlockApiCache.of(RedstoneInterface.LOOKUP, world, target);
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
        RedstoneInterface redstone = redstoneCache.find();
        if (redstone != null)
        {
            plc.variableStack().push(redstone.getReceivedStrength());
        }
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.READ_REDSTONE;
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, com.neep.neepmeat.neepasm.compiler.Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        Argument target = parser.parseArgument(view);
        if (target == null)
            throw new NeepASM.ParseException("expected redstone target");

        return ((world, source, program) ->
        {
            program.addBack(new ReadRedstoneInstruction(() -> world, target));
        });
    }
}
