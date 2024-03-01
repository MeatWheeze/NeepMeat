package com.neep.meatlib.datagen;

import com.google.common.collect.Sets;
import com.neep.meatlib.datagen.loot.BlockLootTableProvider;
import com.neep.meatlib.datagen.loot.MeatlibAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataProvider;

import java.util.Set;

public class MeatLibDataGen implements DataGeneratorEntrypoint
{
    private static final Set<FabricDataGenerator.Pack.Factory<?>> FACTORIES = Sets.newHashSet();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator)
    {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(BlockTagProvider::new);
        pack.addProvider(MeatRecipeProvider::new);
//        pack.addProvider(MeatlibAdvancementProvider::new);

        FACTORIES.forEach(pack::addProvider);
    }

    public static <T extends DataProvider> void register(FabricDataGenerator.Pack.Factory<T> factory)
    {
        FACTORIES.add(factory);
    }
}
