package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.processing.BlockCrushingRegistry;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.grinder.CrusherRecipeContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class BlockCrushingRecipe extends AdvancedCrushingRecipe
{
    private final long mainAmount;
    private final long extraAmount;
    private final float outputChance;

    @Nullable
    public static BlockCrushingRecipe get(RecipeManager recipeManager)
    {
        return (BlockCrushingRecipe) recipeManager.get(new Identifier(NeepMeat.NAMESPACE, "block_crushing")).orElse(null);
    }

    public BlockCrushingRecipe(Identifier id, long mainAmount, long extraAmount, float outputChance)
    {
        super(id, RecipeInputs.empty(), RecipeOutput.empty(), RecipeOutput.empty(), 5, 40);
        this.mainAmount = mainAmount;
        this.extraAmount = extraAmount;
        this.outputChance = outputChance;
    }

    @Nullable
    protected BlockCrushingRegistry.Entry getFromInput(ItemVariant input)
    {
        return BlockCrushingRegistry.INSTANCE.getFromInputBasic(input);
    }

    @Override
    public boolean matches(CrusherRecipeContext storage)
    {
        for (var view : storage.getInputStorage())
        {
            if (getFromInput(view.getResource()) != null)
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
            BlockCrushingRegistry.Entry entry = getFromInput(resource);
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
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.BLOCK_CRUSHING_SERIALIZER;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.GRINDING;
    }

    @Override
    public boolean ejectOutputs(CrusherRecipeContext storage, TransactionContext transaction)
    {
        for (var view : storage.getInputStorage())
        {
            ItemVariant resource = view.getResource();
            BlockCrushingRegistry.Entry entry = getFromInput(resource);
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

    public long getBaseAmount()
    {
        return mainAmount;
    }

    public long getExtraAmount()
    {
        return extraAmount;
    }

    public float getChance()
    {
        return outputChance;
    }

    public static class Serialiser<T extends BlockCrushingRecipe> implements MeatRecipeSerialiser<T>
    {
        private final Constructor<T> constructor;

        public Serialiser(Constructor<T> constructor)
        {
            this.constructor = constructor;
        }

        @Override
        public T read(Identifier id, JsonObject json)
        {
            int mainAmount = JsonHelper.getInt(json, "base_amount");
            int extraAmount = JsonHelper.getInt(json, "extra_amount");
            float outputChance = JsonHelper.getFloat(json, "extra_chance");
            return constructor.create(id, mainAmount, extraAmount, outputChance);
        }

        @Override
        public T read(Identifier id, PacketByteBuf buf)
        {
            return constructor.create(id, buf.readLong(), buf.readLong(), buf.readFloat());
        }

        @Override
        public void write(PacketByteBuf buf, BlockCrushingRecipe recipe)
        {
            buf.writeLong(recipe.mainAmount);
            buf.writeLong(recipe.extraAmount);
            buf.writeFloat(recipe.outputChance);
        }

        @FunctionalInterface
        public interface Constructor<T extends BlockCrushingRecipe>
        {
            T create(Identifier id, long mainAmount, long extraAmount, float outputChance);
        }
    }
}
