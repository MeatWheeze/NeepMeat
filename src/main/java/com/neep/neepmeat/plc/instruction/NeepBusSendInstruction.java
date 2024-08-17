package com.neep.neepmeat.plc.instruction;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.AtomicAction;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.ParsedInstruction;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class NeepBusSendInstruction implements Instruction
{
    private final Supplier<World> worldSupplier;
    private final String address;

    public NeepBusSendInstruction(Supplier<World> worldSupplier, NbtCompound nbt)
    {
        this(worldSupplier, nbt.getString("address"));
    }

    public NeepBusSendInstruction(Supplier<World> worldSupplier, String address)
    {
        this.worldSupplier = worldSupplier;
        this.address = address;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putString("address", address);
        return nbt;
    }

    @Override
    public void start(PLC plc)
    {
        plc.addRobotAction(AtomicAction.of(p ->
        {
            if (p instanceof PLCBlockEntity be)
            {
                be.getSender().send(address, p.variableStack().popInt());
            }
        }), PLC::advanceCounter);
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.NEEPBUS_SEND;
    }

    public static ParsedInstruction parser(TokenView view, ParsedSource parsedSource, Parser parser, @Nullable String s) throws NeepASM.ParseException
    {
        String address = view.nextString();
        if (address == null)
            throw new NeepASM.ParseException("Expected address string");

        return (world, parsedSource1, program) ->
        {
            program.addBack(new NeepBusSendInstruction(() -> world, address));
        };
    }
}
