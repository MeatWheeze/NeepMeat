package com.neep.neepmeat.datagen;

import com.neep.meatlib.datagen.MeatLibDataGen;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class NMItemTagProvider extends FabricTagProvider.ItemTagProvider
{
    public NMItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    public static void init()
    {
        MeatLibDataGen.register(NMItemTagProvider::new);
    }

    @Override
    public String getName()
    {
        return "Tags for " + this.registryRef.getValue() + " (" + NeepMeat.NAMESPACE + ")";
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        getOrCreateTagBuilder(NMTags.CHARNEL_COMPACTOR)
                .addOptionalTag(NMTags.RAW_MEAT)
                .addOptionalTag(NMTags.RAW_FISH)
                .addOptionalTag(TagKey.of(Registries.ITEM.getKey(), new Identifier("chestcavity", "salvageable_human_organ_meat")))
                .addOptionalTag(TagKey.of(Registries.ITEM.getKey(), new Identifier("chestcavity", "salvageable_animal_organ_meat")))
                .addOptionalTag(TagKey.of(Registries.ITEM.getKey(), new Identifier("chestcavity", "salvageable_rotten_flesh")))
                .add(Items.BONE, Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, Items.EGG, Items.TURTLE_EGG, Items.FROGSPAWN)
                .add(NMItems.ASSORTED_BIOMASS)
                .add(Items.SNIFFER_EGG)
        ;

        getOrCreateTagBuilder(NMTags.BLOCK_CRUSHING_OUTPUTS)
                .addOptionalTag(ConventionalItemTags.RAW_ORES)
                .addOptionalTag(TagKey.of(Registries.ITEM.getKey(), new Identifier("mythicmetals", "raw_ores")))
                ;
    }
}
