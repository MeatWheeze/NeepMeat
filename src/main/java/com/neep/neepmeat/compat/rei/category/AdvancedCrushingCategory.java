package com.neep.neepmeat.compat.rei.category;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.rei.NMREIPlugin;
import com.neep.neepmeat.compat.rei.display.GrindingDisplay;
import com.neep.neepmeat.init.NMBlocks;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.Text;

public class AdvancedCrushingCategory extends GrindingCategory
{
    @Override
    public CategoryIdentifier<? extends GrindingDisplay> getCategoryIdentifier()
    {
        return NMREIPlugin.ADVANCED_CRUSHING;
    }

    @Override
    public Renderer getIcon()
    {
        return EntryStacks.of(NMBlocks.LARGE_CRUSHER);
    }

    @Override
    public Text getTitle()
    {
        return Text.translatable("category." + NeepMeat.NAMESPACE + ".advanced_crushing");
    }
}
