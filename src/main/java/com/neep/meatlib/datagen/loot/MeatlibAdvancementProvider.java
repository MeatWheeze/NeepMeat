package com.neep.meatlib.datagen.loot;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MeatlibAdvancementProvider extends FabricAdvancementProvider
{
    public MeatlibAdvancementProvider(FabricDataOutput output)
    {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer)
    {
//        for (var subsidiary : SUBSIDIARY)
//        {
//            subsidiary.accept(consumer);
//        }
    }

//    @FunctionalInterface
//    public interface Subsidiary extends Consumer<Consumer<Advancement>> { }
//
//    protected static List<Subsidiary> SUBSIDIARY = new ArrayList<>();
//
//    public static void addSubsidiary(Subsidiary consumer)
//    {
//        SUBSIDIARY.add(consumer);
//    }
}
