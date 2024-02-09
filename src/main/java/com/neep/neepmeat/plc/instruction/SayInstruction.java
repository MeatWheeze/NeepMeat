package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
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
    private final char format;

    public SayInstruction(String message)
    {
        this.message = message;
        this.format = 'd';
    }

    public SayInstruction(char format)
    {
        this.format = format;
        this.message = "";
    }

    public SayInstruction(Supplier<World> worldSupplier, NbtCompound nbtCompound)
    {
        this.message = nbtCompound.getString("message");
        this.format = (char) nbtCompound.getShort("format");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putString("message", message);
        nbt.putShort("format", (short) format);
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        if (plc instanceof BlockEntity be)
        {
            if (message.isEmpty())
            {
                int value = plc.variableStack().popInt();
                String string = format == 'b' ? Integer.toBinaryString(value) :
                        format == 'h' ? Integer.toHexString(value) :
                        String.valueOf(value);

                PlayerLookup.around((ServerWorld) be.getWorld(), be.getPos(), 20).forEach(p -> p.sendMessage(
                        Text.of("[PLC at " + be.getPos().getX() + " " + be.getPos().getY() + " " + be.getPos().getZ() + "] " + string)));

            }
            else
            {
                PlayerLookup.around((ServerWorld) be.getWorld(), be.getPos(), 20).forEach(p -> p.sendMessage(
                        Text.of("[PLC at " + be.getPos().getX() + " " + be.getPos().getY() + " " + be.getPos().getZ() + "] " + message)));
            }
        }
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.SAY;
    }

    public static class Parser implements InstructionParser
    {
        @Override
        public ParsedInstruction parse(TokenView view, ParsedSource parsedSource, com.neep.neepmeat.neepasm.compiler.Parser parser) throws NeepASM.ParseException
        {
            view.fastForward();

            char c = view.peek();

            if (c == '"')
            {
                String message = view.nextString();
                if (!message.isEmpty())
                {
                    view.next();
                    parser.assureLineEnd(view);
                    return ((world, source, program) -> program.addBack(new SayInstruction(message)));
                }
                throw new NeepASM.ParseException("expected message string");
            }

            char format;

            if (TokenView.isIdentifier(0, c))
            {
                if (!(c == 'd' || c == 'h' || c == 'b'))
                    throw new NeepASM.ParseException("format must be 'd', 'h', or 'b'");

                format = c;
                view.next();
            }
            else
            {
                format = 'd';
            }


            parser.assureLineEnd(view);
            return ((world, source, program) -> program.addBack(new SayInstruction(format)));
        }
    }
}
