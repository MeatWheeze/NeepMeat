package com.neep.neepmeat.datagen;

import com.neep.meatlib.datagen.MeatLibDataGen;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.plc.PLCBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.ItemCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class NMAdvancements extends FabricAdvancementProvider
{
    protected NMAdvancements(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer)
    {
        Advancement root = Advancement.Builder.create()
                .display(
                        NMItems.RAW_MEAT_BRICK,
                        Text.translatable("advancements.neepmeat.main.root.title"),
                        Text.translatable("advancements.neepmeat.main.root.description"),
                        new Identifier("neepmeat:textures/gui/advancements/backgrounds/main.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false

                )
                .criterion("immediate", TickCriterion.Conditions.createTick())
                .build(new Identifier(NeepMeat.NAMESPACE, "root"));
        consumer.accept(root);

        Advancement obtain_guide = getItem(
                consumer, "main", "obtain_guide", root,
                NMItems.ENLIGHTENED_BRAIN
        );

        Advancement obtain_integrator = getItem(
                consumer, "main", "obtain_integrator", root,
                NMBlocks.INTEGRATOR_EGG
        );

        Advancement enlightenment = getItem(
                consumer, "main", "enlightenment", obtain_integrator,
                NMBlocks.PEDESTAL
        );

        Advancement obtain_enlightened_brain = getItem(
                consumer, "main", "obtain_enlightened_brain", enlightenment,
                NMItems.ENLIGHTENED_BRAIN
            );

        Advancement place_grinder = placeBlock(
                consumer, "main", "place_grinder", obtain_integrator,
                NMBlocks.GRINDER
        );

        Advancement place_hydraulic_press = getItem(
                consumer, "main", "place_hydraulic_press", place_grinder,
                NMBlocks.HYDRAULIC_PRESS
        );

        Advancement obtain_meat_bucket = getItem(
                consumer, "main", "obtain_meat_bucket", place_grinder,
                NMFluids.MEAT_BUCKET
        );


        Advancement obtain_plc = placeBlock(
                consumer, "main", "obtain_plc", enlightenment,
                PLCBlocks.PLC
        );

        Advancement obtain_power_flower_seeds = getItem(
                consumer, "main", "obtain_power_flower_seeds", obtain_plc,
                NMBlocks.POWER_FLOWER_SEEDS
        );

        Advancement obtain_whisper_flour = getItem(
                consumer, "main", "obtain_whisper_flour", root,
                NMItems.WHISPER_FLOUR
        );

        Advancement obtain_whisper_brass = getItem(
                consumer, "main", "obtain_whisper_brass", obtain_whisper_flour,
                NMItems.WHISPER_BRASS
        );

        Advancement place_ejector = getItem(
                consumer, "main", "place_ejector", obtain_whisper_brass,
                NMBlocks.EJECTOR
        );

        Advancement place_item_pipe = placeBlock(
                consumer, "main", "place_item_pipe", obtain_whisper_brass,
                NMBlocks.PNEUMATIC_TUBE
        );
    }

    private static Advancement getItem(Consumer<Advancement> consumer, String id, Advancement parent, Text name, Text desc, ItemConvertible... item)
    {
        Advancement advancement = Advancement.Builder.create()
                .parent(parent)
                .display(item[0], name, desc, null, AdvancementFrame.TASK, true, false, false)
                .criterion("get_item", InventoryChangedCriterion.Conditions.items(item))
                .build(new Identifier(NeepMeat.NAMESPACE, id));

        consumer.accept(advancement);

        return advancement;
    }

    private static Advancement getItem(Consumer<Advancement> consumer, String prefix, String id, Advancement parent, ItemConvertible... item)
    {
        Advancement advancement = Advancement.Builder.create()
                .parent(parent)
                .display(item[0],
                        Text.translatable("advancements." + NeepMeat.NAMESPACE + "." + prefix + "." + id + ".title"),
                        Text.translatable("advancements." + NeepMeat.NAMESPACE + "." + prefix + "." + id + ".desc"),
                        null, AdvancementFrame.TASK, true, false, false)
                .criterion("get_item", InventoryChangedCriterion.Conditions.items(item))
                .build(new Identifier(NeepMeat.NAMESPACE, id));

        consumer.accept(advancement);

        return advancement;
    }

    private static Advancement placeBlock(Consumer<Advancement> consumer, String prefix, String id, Advancement parent, Block block)
    {
        Advancement advancement = Advancement.Builder.create()
                .parent(parent)
                .display(block,
                        Text.translatable("advancements." + NeepMeat.NAMESPACE + "." + prefix + "." + id + ".title"),
                        Text.translatable("advancements." + NeepMeat.NAMESPACE + "." + prefix + "." + id + ".desc"),
                        null, AdvancementFrame.TASK, true, false, false)
                .criterion("get_item", ItemCriterion.Conditions.createPlacedBlock(block))
                .build(new Identifier(NeepMeat.NAMESPACE, id));

        consumer.accept(advancement);

        return advancement;
    }

    public static void init()
    {
        MeatLibDataGen.register(NMAdvancements::new);
    }
}
