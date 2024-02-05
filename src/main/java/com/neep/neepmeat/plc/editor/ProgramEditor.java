package com.neep.neepmeat.plc.editor;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.plc.program.PLCProgram;
import com.neep.neepmeat.api.storage.WorldSupplier;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.PLCCompiler;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.program.PLCProgramImpl;
import net.minecraft.nbt.NbtCompound;

public class ProgramEditor implements NbtSerialisable
{
    private String programSource = "";

    private final Parser parser = new Parser();
    private final PLCCompiler compiler;
    private final PLCBlockEntity plc;

    private PLCProgram program;

    public ProgramEditor(PLCBlockEntity plc)
    {
        compiler = new PLCCompiler(WorldSupplier.of(plc));
        this.program = new PLCProgramImpl(plc::getWorld);
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

    public PLCProgram getProgram()
    {
        if (program == null)
            return PLCProgram.EMPTY;

        return program;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putString("source", programSource);
        nbt.put("program", program.writeNbt(new NbtCompound()));

        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.programSource = nbt.getString("source");
        this.program.readNbt(nbt.getCompound("program"));
    }
}
