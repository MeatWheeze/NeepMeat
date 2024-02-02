package com.neep.neepmeat.plc.editor;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.plc.program.PlcProgram;
import com.neep.neepmeat.api.storage.WorldSupplier;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.PLCCompiler;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

public class ProgramEditor implements NbtSerialisable
{
    private String programSource = "";

    private final Parser parser = new Parser();
    private final PLCCompiler compiler;
    private final PLCBlockEntity plc;

    @Nullable
    private PlcProgram program;

    public ProgramEditor(PLCBlockEntity plc)
    {
        compiler = new PLCCompiler(WorldSupplier.of(plc));
        this.plc = plc;
    }

    public void setProgramSource(String text)
    {
        this.programSource = text;
    }

    public String getSource()
    {
        return programSource;
    }

    public void compile()
    {
        try
        {
            var parsed = parser.parse(programSource);
            this.program = compiler.compile(parsed);
        }
        catch (NeepASM.ProgramBuildException e)
        {
            e.printStackTrace();
        }
    }

    public PlcProgram getProgram()
    {
        if (program == null)
            return PlcProgram.EMPTY;

        return program;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return null;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }
}
