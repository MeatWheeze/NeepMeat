package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.api.processing.BlockCrushingRegistry;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.grinder.CrusherRecipeContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class BlockCrushingRecipe extends CrushingRecipe
{
    private BlockCrushingRecipe(Identifier id)
    {
        super(id, RecipeInputs.empty(), RecipeOutput.empty(), RecipeOutput.empty(), 5, 40);
    }

    @Override
    public boolean matches(CrusherRecipeContext storage)
    {
        for (var view : storage.getInputStorage())
        {
            if (BlockCrushingRegistry.INSTANCE.getFromInput(view.getResource()) != null)
                return true;
        }
        return super.matches(storage);
    }

    @Override
    public boolean takeInputs(CrusherRecipeContext storage, TransactionContext transaction)
    {
        for (var view : storage.getInputStorage())
        {
            ItemVariant resource = view.getResource();
            BlockCrushingRegistry.Entry entry = BlockCrushingRegistry.INSTANCE.getFromInput(resource);
            if (entry != null)
            {
                Item item = resource.getItem();
                try (Transaction inner = transaction.openNested())
                {
                    long extracted = storage.getInputStorage().extract(ItemVariant.of(item), entry.input().amount(), transaction);
                    if (extracted == itemInput.amount())
                    {
                        inner.commit();
                        return true;
                    }
                    inner.abort();
                }
                return false;
            }
        }

        return false;
    }

    @Override
    public boolean ejectOutputs(CrusherRecipeContext storage, TransactionContext transaction)
    {
        for (var view : storage.getInputStorage())
        {
            ItemVariant resource = view.getResource();
            BlockCrushingRegistry.Entry entry = BlockCrushingRegistry.INSTANCE.getFromInput(resource);
            if (entry != null)
            {
                try (Transaction inner = transaction.openNested())
                {
                    boolean b1 = entry.output().insertInto(storage.getOutputStorage(), ItemVariant::of, inner);
                    boolean b2 = entry.extra().insertInto(storage.getOutputStorage(), ItemVariant::of, storage.getChanceMod(), inner);
                    if (b1 && b2)
                    {
                        inner.commit();
                        return true;
                    }
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.ADVANCED_CRUSHING;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.BLOCK_CRUSHING_SERIALIZER;
    }

    public static class BlockCrushingSerialiser implements MeatRecipeSerialiser<BlockCrushingRecipe>
    {
        @Override
        public BlockCrushingRecipe read(Identifier id, JsonObject var2)
        {
            return new BlockCrushingRecipe(id);
        }

        @Override
        public BlockCrushingRecipe read(Identifier id, PacketByteBuf buf)
        {
            return new BlockCrushingRecipe(id);
        }

        @Override
        public void write(PacketByteBuf buf, BlockCrushingRecipe recipe)
        {

        }
    }
}
