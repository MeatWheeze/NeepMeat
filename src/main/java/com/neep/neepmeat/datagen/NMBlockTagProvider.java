package com.neep.neepmeat.datagen;

import com.neep.meatlib.datagen.MeatLibDataGen;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;

public class NMBlockTagProvider extends FabricTagProvider.BlockTagProvider
{
    public NMBlockTagProvider(FabricDataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    public static void init()
    {
        MeatLibDataGen.register(NMBlockTagProvider::new);
    }

    @Override
    public String getName()
    {
        return "Tags for " + this.registry.getKey().getValue() + " (" + NeepMeat.NAMESPACE + ")";
    }

    @Override
    protected void generateTags()
    {
        // Blocks whose corresponding items will be used as recipe inputs for crushing if their loot table
        // contains a raw ore.
        getOrCreateTagBuilder(NMTags.BLOCK_CRUSHING_INPUTS)
                .addOptionalTag(ConventionalBlockTags.ORES)
        ;

        getOrCreateTagBuilder(NMTags.CHARNEL_PUMP_OUTPUT_ORES)
                .addOptionalTag(ConventionalBlockTags.ORES);
    }
}
