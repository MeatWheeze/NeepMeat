package com.neep.neepmeat.compat.rei;

import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.neepmeat.compat.rei.display.*;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.plugins.REIServerPlugin;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NMCommonPlugin implements REIServerPlugin, NMREIPlugin
{
    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry)
    {
        registry.register(GRINDING, GrindingDisplay.serializer(GRINDING));
        registry.register(ADVANCED_CRUSHING, GrindingDisplay.serializer(ADVANCED_CRUSHING));
        registry.register(COMPACTING, CompactingDisplay.serializer());
        registry.register(MIXING, MixingDisplay.serializer());
        registry.register(ALLOY_SMELTING, AlloySmeltingDisplay.serializer());
        registry.register(VIVISECTION, VivisectionDisplay.serializer());
        registry.register(ENLIGHTENING, EnlighteningDisplay.serializer());
        registry.register(PRESSING, PressingDisplay.serializer());
        registry.register(SURGERY, SurgeryDisplay.getSerializer());
        registry.registerNotSerializable(ITEM_MANUFACTURE);
        registry.registerNotSerializable(ENTITY_TO_ITEM);
        registry.register(TRANSFORMING_TOOL, TransformingToolDisplay.serializer());
        registry.register(TROMMEL, TrommelDisplay.serializer());
    }

    public static EntryIngredient inputToIngredient(RecipeInput<Item> input)
    {
        return EntryIngredients.ofItems(new ArrayList<>(input.getAll()), (int) input.amount());
    }
}
