package com.neep.neepmeat.plc;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.instruction.Instruction;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import com.neep.neepmeat.api.plc.instruction.InstructionProviderImpl;
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

//    public static final Registry<ImmediateInstructionProvider> IMMEDIATE = FabricRegistryBuilder.createSimple(
//            ImmediateInstructionProvider.class,
//            new Identifier(NeepMeat.NAMESPACE, "immediate_instruction_provider")).buildAndRegister();


    public static final InstructionProvider END = register("end", new InstructionProviderImpl((w, a) -> Instruction.end(), (w, n) -> Instruction.end(), 0, Text.of("END")));
    public static final InstructionProvider GOTO_START = register("goto_start", new InstructionProviderImpl((w, a) -> RestartInstruction.INSTANCE, (w, n) -> RestartInstruction.INSTANCE, 0, Text.of("RESTART")));
    public static final InstructionProvider COMBINE = register("combine", new InstructionProviderImpl(CombineInstruction::new, CombineInstruction::new, 2, Text.of("COMBINE")));
    public static final InstructionProvider MOVE = register("move", new InstructionProviderImpl(MoveInstruction::new, MoveInstruction::new, 2, Text.of("MOVE")));

    private static InstructionProvider register(String path, InstructionProvider provider)
    {
//        if (provider instanceof ImmediateInstructionProvider immediate)
//        {
//            return Registry.register(IMMEDIATE, new Identifier(NeepMeat.NAMESPACE, path), immediate);
//        }
        return Registry.register(REGISTRY, new Identifier(NeepMeat.NAMESPACE, path), provider);
    }
}
