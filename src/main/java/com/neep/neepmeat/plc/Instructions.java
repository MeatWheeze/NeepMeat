package com.neep.neepmeat.plc;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.instruction.*;
import com.neep.neepmeat.plc.instruction.*;
import com.neep.neepmeat.plc.instruction.CombineInstruction;
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

//    public static final Registry<ImmediateInstructionProvider> IMMEDIATE = FabricRegistryBuilder.createSimple(
//            ImmediateInstructionProvider.class,
//            new Identifier(NeepMeat.NAMESPACE, "immediate_instruction_provider")).buildAndRegister();


    public static final InstructionProvider END = register("end", new SimpleInstructionProvider((w, a) -> Instruction.end(), (w, n) -> Instruction.end(), 0, Text.of("END")));
    public static final InstructionProvider GOTO_START = register("goto_start", new SimpleInstructionProvider((w, a) -> RestartInstruction.INSTANCE, (w, n) -> RestartInstruction.INSTANCE, 0, Text.of("RESTART")));
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

    public static final InstructionProvider WAIT_REDSTONE = register("wait_redstone", new SimpleInstructionProvider(WaitRedstoneInstruction::new, WaitRedstoneInstruction::new, 1, Text.of("AWAIT REDSTONE")));

    private static <T extends InstructionProvider> T register(String path, T provider)
    {
//        if (provider instanceof ImmediateInstructionProvider immediate)
//        {
//            return Registry.register(IMMEDIATE, new Identifier(NeepMeat.NAMESPACE, path), immediate);
//        }
        return Registry.register(REGISTRY, new Identifier(NeepMeat.NAMESPACE, path), provider);
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
