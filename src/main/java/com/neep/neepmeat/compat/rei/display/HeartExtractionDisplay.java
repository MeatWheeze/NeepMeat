package com.neep.neepmeat.compat.rei.display;

import com.neep.neepmeat.compat.rei.NMREIPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HeartExtractionDisplay extends BasicDisplay
{
    protected List<Text> entities;

    public static HeartExtractionDisplay of(List<EntityType<?>> inputs, List<EntryIngredient> output)
    {
        NbtList list = new NbtList();
        for (EntityType<?> entry : inputs)
        {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("entity", entry.getTranslationKey());
            list.add(nbt);
        }
        NbtCompound compound = new NbtCompound();
        compound.put("list", list);
        return new HeartExtractionDisplay(Collections.emptyList(), output, compound);
    }

    public HeartExtractionDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, NbtCompound nbt)
    {
        super(inputs, outputs);
        this.entities = ((NbtList) nbt.get("list")).stream().map(e -> Text.translatable(((NbtCompound) e).getString("entity"))).collect(Collectors.toList());
    }

    public static Serializer<HeartExtractionDisplay> serializer()
    {
        return Serializer.ofRecipeLess(HeartExtractionDisplay::new);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier()
    {
        return NMREIPlugin.HEART_EXTRACTION;
    }

    public List<Text> getEntities()
    {
        return entities;
    }
}
