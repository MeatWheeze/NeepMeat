package com.neep.meatlib.datagen;

import com.neep.meatlib.datagen.loot.BlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MeatLibDataGen implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        fabricDataGenerator.addProvider(BlockLootTableProvider::new);
        fabricDataGenerator.addProvider(BlockTagProvider::new);
        fabricDataGenerator.addProvider(MeatRecipeProvider::new);
    }
}
