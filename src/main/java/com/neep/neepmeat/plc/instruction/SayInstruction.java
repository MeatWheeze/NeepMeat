package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.AtomicAction;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.InstructionParser;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SayInstruction implements Instruction
{
    private final String message;

    public SayInstruction(String message)
    {
        this.message = message;
    }

    public SayInstruction(Supplier<World> worldSupplier, NbtCompound nbtCompound)
    {
        this.message = nbtCompound.getString("message");
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(new SayAction(), this::finish);
    }

    private void finish(PLC plc)
    {
        plc.advanceCounter();
    }

    @Override
    public void cancel(PLC plc)
    {

    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.SAY;
    }

    private class SayAction implements AtomicAction
    {
        @Override
        public void start(PLC plc)
        {
            BlockEntity be = (BlockEntity) plc;
            PlayerLookup.around((ServerWorld) be.getWorld(), be.getPos(), 20).forEach(p -> p.sendMessage(Text.of(message)));
        }
    }

    public static class Parser implements InstructionParser
    {
        @Override
        public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, com.neep.neepmeat.neepasm.compiler.Parser parser) throws NeepASM.ParseException
        {
            view.fastForward();
            String message = view.nextString();
            if (message.isEmpty())
                throw new NeepASM.ParseException("invalid message string");

            return ((world, source, program) -> program.addBack(new SayInstruction(message)));
        }
    }
}
