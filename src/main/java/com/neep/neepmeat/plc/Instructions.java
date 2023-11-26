package com.neep.neepmeat.plc;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.opcode.InstructionProvider;
import com.neep.neepmeat.plc.program.CombineInstruction;
import com.neep.neepmeat.plc.program.PLCInstruction;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Instructions
{
    public static final Registry<InstructionProvider> REGISTRY = FabricRegistryBuilder.createSimple(InstructionProvider.class,
            new Identifier(NeepMeat.NAMESPACE, "instruction_provider")).buildAndRegister();


    public static final InstructionProvider END = register("end", new InstructionProvider((w, a) -> PLCInstruction.end(), (w, n) -> PLCInstruction.end(), 0, Text.of("END")));
    public static final InstructionProvider COMBINE = register("combine", new InstructionProvider(CombineInstruction::new, CombineInstruction::new, 2, Text.of("COMBINE")));

    private static InstructionProvider register(String path, InstructionProvider provider)
    {
        return Registry.register(REGISTRY, new Identifier(NeepMeat.NAMESPACE, path), provider);
    }
}
