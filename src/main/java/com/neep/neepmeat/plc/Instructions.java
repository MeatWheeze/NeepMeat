package com.neep.neepmeat.plc;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.instruction.CallInstruction;
import com.neep.neepmeat.api.plc.instruction.PredicatedInstructionBuilder;
import com.neep.neepmeat.api.plc.instruction.SimpleInstructionProvider;
import com.neep.neepmeat.api.plc.instruction.SimplerInstructionProvider;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.neepasm.compiler.TokenView;
import com.neep.neepmeat.neepasm.compiler.parser.CallInstructionParser;
import com.neep.neepmeat.neepasm.compiler.parser.InstructionParser;
import com.neep.neepmeat.neepasm.compiler.parser.JumpInstructionParser;
import com.neep.neepmeat.plc.instruction.*;
import com.neep.neepmeat.plc.instruction.SayInstruction;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class Instructions
{
    public static final Registry<InstructionProvider> REGISTRY = FabricRegistryBuilder.createSimple(
            InstructionProvider.class,
            new Identifier(NeepMeat.NAMESPACE, "instruction_provider")).buildAndRegister();

    public static final InstructionProvider END = register("end", new SimpleInstructionProvider((w, a) -> Instruction.end(), (w, n) -> Instruction.end(), 0, Text.of("END")));
    public static final InstructionProvider GOTO_START = register("goto_start", new SimpleInstructionProvider((w, a) -> RestartInstruction.INSTANCE, (w, n) -> RestartInstruction.INSTANCE, 0, Text.of("RESTART")));

    public static final SimplerInstructionProvider RET = register("ret", new SimplerInstructionProvider(ReturnInstruction::new, parseNoArguments(ReturnInstruction::new), Text.of("RET")));
    public static final SimplerInstructionProvider CALL = register("call", new SimplerInstructionProvider(CallInstruction::new, new CallInstructionParser(), Text.of("CALL")));
    public static final SimplerInstructionProvider PUSH = register("push", new SimplerInstructionProvider(PushInstruction::new, new PushInstruction.Parser(), Text.of("PUSH")));
    public static final SimplerInstructionProvider POP = register("pop", new SimplerInstructionProvider(PopInstruction::new, parseNoArguments(PopInstruction::new), Text.of("POP")));

    public static final SimplerInstructionProvider EQ = register("eq", new SimplerInstructionProvider((w, n) -> new ComparisonInstruction.Equals(), parseNoArguments(ComparisonInstruction.Equals::new), Text.of("EQ")));
    public static final SimplerInstructionProvider LT = register("lt", new SimplerInstructionProvider((w, n) -> new ComparisonInstruction.LessThan(), parseNoArguments(ComparisonInstruction.LessThan::new), Text.of("LT")));
    public static final SimplerInstructionProvider LTEQ = register("lteq", new SimplerInstructionProvider((w, n) -> new ComparisonInstruction.LessThanEqual(), parseNoArguments(ComparisonInstruction.LessThanEqual::new), Text.of("LTEQ")));
    public static final SimplerInstructionProvider GT = register("gt", new SimplerInstructionProvider((w, n) -> new ComparisonInstruction.GreaterThan(), parseNoArguments(ComparisonInstruction.GreaterThan::new), Text.of("GT")));
    public static final SimplerInstructionProvider GTEQ = register("gteq", new SimplerInstructionProvider((w, n) -> new ComparisonInstruction.GreaterThanEqual(), parseNoArguments(ComparisonInstruction.GreaterThanEqual::new), Text.of("GTEQ")));

    public static final InstructionProvider JUMP = register("jump", new SimplerInstructionProvider(JumpInstruction::new, new JumpInstructionParser(JumpInstruction::new), Text.of("JMP")));
    public static final SimplerInstructionProvider BIT = register("bit", new SimplerInstructionProvider(BITInstruction::new, new JumpInstructionParser(BITInstruction::new), Text.of("BIT")));

    public static final SimplerInstructionProvider SAY = register("say", new SimplerInstructionProvider(SayInstruction::new, new SayInstruction.Parser(), Text.of("SAY")));
    public static final InstructionProvider REMOVE = register("remove", new SimpleInstructionProvider(RemoveInstruction::new, RemoveInstruction::new, 1, Text.of("REMOVE")));

    public static final InstructionProvider COMBINE = register("combine", new SimpleInstructionProvider(CombineInstruction::new, CombineInstruction::new, 2, Text.of("COMBINE"))
            .factory(PredicatedInstructionBuilder.create()
                    .arg(ArgumentPredicates.IS_ITEM_STORAGE)
                    .arg(ArgumentPredicates.IS_ITEM_MIP) ));
    public static final InstructionProvider MOVE = register("move", new SimpleInstructionProvider(MoveInstruction::new, MoveInstruction::new, 2, Text.of("MOVE"))
            .factory(PredicatedInstructionBuilder.create()
                    .arg(ArgumentPredicates.IS_ITEM_STORAGE)
                    .arg(ArgumentPredicates.IS_ITEM_STORAGE)));
    public static final InstructionProvider IMPLANT = register("implant", new SimpleInstructionProvider(ImplantInstruction::new, ImplantInstruction::new, 2, Text.of("IMPLANT"))
            .factory(PredicatedInstructionBuilder.create()
                    .arg(ArgumentPredicates.IS_ITEM_STORAGE)
                    .arg(ArgumentPredicates.IS_ENTITY_MIP)));
    public static final InstructionProvider INJECT = register("inject", new SimpleInstructionProvider(InjectInstruction::new, InjectInstruction::new, 2, Text.of("INJECT"))
            .factory(PredicatedInstructionBuilder.create()
                    .arg(ArgumentPredicates.IS_FLUID_STORAGE)
                    .arg(ArgumentPredicates.IS_ITEM_MIP)));

    public static final InstructionProvider WAIT_REDSTONE = register("wait_redstone", new SimpleInstructionProvider(WaitRedstoneInstruction::new, WaitRedstoneInstruction::new, 1, Text.of("R_WAIT")));

    private static <T extends InstructionProvider> T register(String path, T provider)
    {
//        if (provider instanceof ImmediateInstructionProvider immediate)
//        {
//            return Registry.register(IMMEDIATE, new Identifier(NeepMeat.NAMESPACE, path), immediate);
//        }
        return Registry.register(REGISTRY, new Identifier(NeepMeat.NAMESPACE, path), provider);
    }

    /**
     * @param supplier Constructor of the desired instruction
     * @return An InstructionParser that expects a blank line (disregarding comments) after the operation.
     */
    static InstructionParser parseNoArguments(Supplier<Instruction> supplier)
    {
        return (TokenView view, ParsedSource parsedSource, Parser parser) ->
        {
            view.fastForward();
            if (!view.lineEnded() && !parser.isComment(view))
                throw new NeepASM.ParseException("unexpected token '" + view.nextBlob() + "'");

            return (world, source, program) ->
                    program.addBack(supplier.get());
        };
    }

    public static ResourceAmount<ItemVariant> takeItem(Argument target, Supplier<World> world, int count)
    {
        var storage = ItemStorage.SIDED.find(world.get(), target.pos(), target.face());
        if (storage != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ResourceAmount<ItemVariant> found = StorageUtil.findExtractableContent(storage, transaction);
                if (found != null)
                {
                    long extracted = storage.extract(found.resource(), Math.min(found.amount(), 64), transaction);
                    if (extracted > 0)
                    {
                        var res = new ResourceAmount<>(found.resource(), extracted);
                        transaction.commit();
                        return res;
                    }

                    transaction.abort();
                }
            }
        }

        return null;
    }

    public static ResourceAmount<FluidVariant> takeFluid(Argument target, Supplier<World> world, long amount)
    {
        var storage = FluidStorage.SIDED.find(world.get(), target.pos(), target.face());
        if (storage != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ResourceAmount<FluidVariant> found = StorageUtil.findExtractableContent(storage, transaction);
                if (found != null)
                {
                    long extracted = storage.extract(found.resource(), Math.min(found.amount(), amount), transaction);
                    if (extracted > 0)
                    {
                        var res = new ResourceAmount<>(found.resource(), extracted);
                        transaction.commit();
                        return res;
                    }

                    transaction.abort();
                }
            }
        }

        return null;
    }
}
