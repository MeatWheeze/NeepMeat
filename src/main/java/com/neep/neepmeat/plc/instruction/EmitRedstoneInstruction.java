package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.RedstoneInterface;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EmitRedstoneInstruction implements Instruction
{
    private final Argument target;
    private final int strength;
    private final LazyBlockApiCache<RedstoneInterface, Direction> redstoneCache;

    public EmitRedstoneInstruction(Supplier<World> world, Argument argument, int strength)
    {
        this.target = argument;
        this.strength = strength;
        this.redstoneCache = LazyBlockApiCache.of(RedstoneInterface.LOOKUP, world, target);
    }

    public EmitRedstoneInstruction(Supplier<World> world, NbtCompound nbt)
    {
        this.target = Argument.fromNbt(nbt.getCompound("target"));
        this.strength = nbt.getInt("strength");
        this.redstoneCache = LazyBlockApiCache.of(RedstoneInterface.LOOKUP, world, target);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("target", target.toNbt());
        nbt.putInt("strength", strength);
        return nbt;
    }

    @Override
    public boolean canStart(PLC plc)
    {
        if (plc instanceof BlockEntity be)
        {
            return be.getWorld().getTime() % 2 == 0;
        }
        return false;
    }

    @Override
    public void start(PLC plc)
    {
        RedstoneInterface redstone = redstoneCache.find();
        if (redstone != null)
        {
            redstone.setEmittedStrength(strength);
        }
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.EMIT_REDSTONE;
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, com.neep.neepmeat.neepasm.compiler.Parser parser) throws NeepASM.ParseException
    {
        view.fastForward();
        Argument target = parser.parseArgument(view);
        if (target == null)
            throw new NeepASM.ParseException("expected redstone target");

        view.fastForward();
        if (!TokenView.isDigit(view.peek()))
            throw new NeepASM.ParseException("expected strength integer");

        int strength = view.nextInteger();

        return ((world, source, program) ->
        {
            program.addBack(new EmitRedstoneInstruction(() -> world, target, strength));
        });
    }
}
