package com.neep.neepmeat.datagen;

import com.neep.neepmeat.datagen.tag.BlockTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class NMDataGen implements DataGeneratorEntrypoint
{
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        System.out.println("datagen                                  -------------------------");
        fabricDataGenerator.addProvider(BlockTagProvider::new);
    }
}