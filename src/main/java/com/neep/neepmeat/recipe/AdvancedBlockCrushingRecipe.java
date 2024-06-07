package com.neep.neepmeat.recipe;

import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.processing.BlockCrushingRegistry;
import com.neep.neepmeat.init.NMrecipeTypes;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AdvancedBlockCrushingRecipe extends BlockCrushingRecipe
{
    @Nullable
    public static AdvancedBlockCrushingRecipe get(RecipeManager recipeManager)
    {
        return (AdvancedBlockCrushingRecipe) recipeManager.get(new Identifier(NeepMeat.NAMESPACE, "advanced_block_crushing")).orElse(null);
    }

    public AdvancedBlockCrushingRecipe(Identifier id, long mainAmount, long extraAmount, float outputChance)
    {
        super(id, mainAmount, extraAmount, outputChance);
    }

    @Nullable
    @Override
    protected BlockCrushingRegistry.Entry getFromInput(ItemVariant input)
    {
        return BlockCrushingRegistry.INSTANCE.getFromInputAdvanced(input);
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.ADVANCED_CRUSHING;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.ADVANCED_BLOCK_CRUSHING_SERIALIZER;
    }
}
