package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.recipe.VivisectionRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

public class VivisectionDisplay extends BasicDisplay
{
    protected final List<Text> entities;

    public VivisectionDisplay(VivisectionRecipe recipe)
    {
        this(List.of(recipe.getEntityType()),
                Collections.singletonList(EntryIngredients.ofItems(List.of(recipe.getReicpeOutput().resource()), (int) recipe.getReicpeOutput().minAmount())),
                null);
    }

    // AAAAAAAAAAAAAAAAAAAAAAAAAA
    public VivisectionDisplay(List<EntityType<?>> entities, List<EntryIngredient> output, Void argh)
    {
        super(Collections.emptyList(), output);
        this.entities = entities.stream().map(e -> (Text) Text.translatable(e.getTranslationKey())).toList();
    }

    public VivisectionDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs)
    {
        super(inputs, outputs);
        this.entities = Collections.emptyList();
    }

    public static VivisectionDisplay of(List<EntityType<?>> inputs, List<EntryIngredient> output)
    {
        return new VivisectionDisplay(inputs, output, null);
    }

    public static VivisectionDisplay of(Item item, List<EntryIngredient> output)
    {
        return new VivisectionDisplay(Collections.singletonList(EntryIngredients.of(item)), output);
    }

    public static Serializer<VivisectionDisplay> serializer()
    {
        return Serializer.ofSimpleRecipeLess(VivisectionDisplay::new);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.VIVISECTION;
    }

    public List<Text> getEntities()
    {
        return entities;
    }
}
