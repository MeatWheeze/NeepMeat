package com.neep.neepmeat.compat.rei.display;

import com.google.common.collect.Lists;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.plc.recipe.EntityToItemRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.entity.EntityType;

import java.util.Collections;
import java.util.List;

public class EntityToItemDisplay extends ManufactureDisplay<EntityType<?>>
{
//    public static final EntryType<EntityType<?>> ENTITY_TYPE_ENTRY_TYPE = EntryType.deferred(new Identifier(NeepMeat.NAMESPACE, "entity_type"));

    private final List<EntryIngredient> outputs;
    private final List<EntryIngredient> inputs;

    public EntityToItemDisplay(EntityToItemRecipe recipe)
    {
        super(recipe.getBase(), recipe.getSteps());
        this.outputs = Collections.singletonList(EntryIngredients.ofItems(List.of(recipe.getRecipeOutput().resource()), (int) recipe.getRecipeOutput().minAmount()));

        this.inputs = Lists.newArrayList();
//        inputs.add(EntryIngredients.of(recipe.getBase()));
        appendStepIngredients(steps, inputs);
    }

    @Override
    public List<EntryIngredient> getInputEntries()
    {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries()
    {
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.ENTITY_TO_ITEM;
    }

//    public static EntryStack<?> entityToStack(EntityType<?> entityType)
//    {
//        return EntryStack.of(ENTITY_TYPE_ENTRY_TYPE, entityType);
//    }
}
