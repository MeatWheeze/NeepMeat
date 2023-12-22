package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;

import java.util.Arrays;
import java.util.List;

public class CompactingDisplay extends BasicDisplay
{
    private int page;

    public static CompactingDisplay of(List<Item> inputs, List<EntryIngredient> output, int page)
    {
        EntryIngredient[] inputIngredients = new EntryIngredient[inputs.size()];
        int i = 0;
        for (ItemConvertible entry : inputs)
        {
            inputIngredients[i] = EntryIngredients.of(entry);
            i++;
        }
        return new CompactingDisplay(Arrays.asList(inputIngredients), output, page);
    }

    public CompactingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, NbtCompound tag)
    {
        this(inputs, outputs, tag.getInt("page"));
    }

    public CompactingDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, int page)
    {
        super(inputs, outputs);
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public static BasicDisplay.Serializer<CompactingDisplay> serializer()
    {
        return BasicDisplay.Serializer.ofRecipeLess(CompactingDisplay::new, (display, tag) ->
                tag.putInt("page", display.page));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.COMPACTING;
    }
}
