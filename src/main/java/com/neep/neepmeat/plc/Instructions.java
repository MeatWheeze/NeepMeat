package com.neep.neepmeat.plc;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.instruction.*;
import com.neep.neepmeat.plc.program.CombineInstruction;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Instructions
{
    public static final Registry<InstructionProvider> REGISTRY = FabricRegistryBuilder.createSimple(
            InstructionProvider.class,
            new Identifier(NeepMeat.NAMESPACE, "instruction_provider")).buildAndRegister();

    public static final Registry<ImmediateInstructionProvider> IMMEDIATE = FabricRegistryBuilder.createSimple(
            ImmediateInstructionProvider.class,
            new Identifier(NeepMeat.NAMESPACE, "immediate_instruction_provider")).buildAndRegister();


    public static final InstructionProvider END = register("end", new InstructionProviderImpl((w, a) -> Instruction.end(), (w, n) -> Instruction.end(), 0, Text.of("END")));
    public static final InstructionProvider COMBINE = register("combine", new InstructionProviderImpl(CombineInstruction::new, CombineInstruction::new, 2, Text.of("COMBINE")));

    private static InstructionProvider register(String path, InstructionProvider provider)
    {
        if (provider instanceof ImmediateInstructionProvider immediate)
        {
            return Registry.register(IMMEDIATE, new Identifier(NeepMeat.NAMESPACE, path), immediate);
        }
        return Registry.register(REGISTRY, new Identifier(NeepMeat.NAMESPACE, path), provider);
    }
}
