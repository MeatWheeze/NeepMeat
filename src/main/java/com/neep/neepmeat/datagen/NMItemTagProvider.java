package com.neep.neepmeat.datagen;

import com.neep.meatlib.datagen.MeatLibDataGen;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class NMItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    public NMItemTagProvider(FabricDataGenerator fabricDataGenerator)
    {
        super(fabricDataGenerator);
    }

    public static void init()
    {
        MeatLibDataGen.register(NMItemTagProvider::new);
    }

    @Override
    public String getName()
    {
        return "Tags for " + this.registry.getKey().getValue() + " (" + NeepMeat.NAMESPACE + ")";
    }

    @Override
    protected void generateTags()
    {
        getOrCreateTagBuilder(NMTags.CHARNEL_COMPACTOR).addOptionalTag(NMTags.RAW_MEAT);
        getOrCreateTagBuilder(NMTags.CHARNEL_COMPACTOR).addOptionalTag(NMTags.RAW_FISH);
        getOrCreateTagBuilder(NMTags.CHARNEL_COMPACTOR).addOptionalTag(TagKey.of(Registry.ITEM.getKey(), new Identifier("chestcavity", "salvageable_human_organ_meat")));
        getOrCreateTagBuilder(NMTags.CHARNEL_COMPACTOR).addOptionalTag(TagKey.of(Registry.ITEM.getKey(), new Identifier("chestcavity", "salvageable_animal_organ_meat")));
        getOrCreateTagBuilder(NMTags.CHARNEL_COMPACTOR).addOptionalTag(TagKey.of(Registry.ITEM.getKey(), new Identifier("chestcavity", "salvageable_rotten_flesh")));

        getOrCreateTagBuilder(NMTags.BLOCK_CRUSHING_OUTPUTS)
                .addOptionalTag(NMTags.RAW_ORES)
                .addOptionalTag(TagKey.of(Registry.ITEM.getKey(), new Identifier("mythicmetals", "raw_ores")))
                ;

        getOrCreateTagBuilder(NMTags.RAW_ORES)
                .addOptionalTag(ConventionalItemTags.RAW_IRON_ORES)
                .addOptionalTag(ConventionalItemTags.RAW_COPPER_ORES)
                .addOptionalTag(ConventionalItemTags.RAW_GOLD_ORES)
        ;
    }
}
