package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.casting_basin.CastingBasinStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

@SuppressWarnings("UnstableApiUsage")
public class FatPressingRecipe extends AbstractPressingRecipe<CastingBasinStorage>
{
    protected Identifier id;
    protected RecipeInput<Fluid> fluidInput;

    public FatPressingRecipe(Identifier id, RecipeInput<Fluid> fluidInput)
    {
        this.fluidInput = fluidInput;
        this.id = id;
    }

    @Override
    public boolean matches(CastingBasinStorage inventory)
    {
        WritableSingleFluidStorage storage = inventory.fluid(null);
        return OreFatRegistry.getFromVariant(storage.getResource()) != null
                && fluidInput.amount() == storage.getAmount();
    }

    public RecipeInput<Fluid> getFluidInput()
    {
        return fluidInput;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.FAT_PRESSING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.FAT_PRESSING;
    }

    public boolean takeInputs(CastingBasinStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            WritableSingleFluidStorage fluidStorage = storage.fluid(null);
            FluidVariant fluidVariant = fluidStorage.getResource();

            // Ensure that storage contents still match the recipe
            OreFatRegistry.Entry entry = OreFatRegistry.getFromVariant(fluidVariant);
            if (entry == null)
            {
                throw new IllegalStateException("Storage contents do not conform to recipe");
            }

            storage.unlock();
            long ex2 = fluidStorage.extract(fluidStorage.getResource(), fluidInput.amount(), inner);
            storage.lock();

            if (ex2 != fluidInput.amount())
            {
                inner.abort();
                return false;
            }

            // Might need to formalise this
            long insertAmount = 1;
            long transferred = storage.item(null).insert(entry.result(), insertAmount, inner);

            if (transferred == insertAmount)
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer implements MeatRecipeSerialiser<FatPressingRecipe>
    {
        RecipeFactory<FatPressingRecipe> factory;

        public Serializer(RecipeFactory<FatPressingRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public FatPressingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject fluidInputElement = JsonHelper.getObject(json, "fluid_input");
            RecipeInput<Fluid> fluidInput = RecipeInput.fromJsonRegistry(RecipeInputs.FLUID, fluidInputElement);

            return this.factory.create(id, fluidInput);
        }

        @Override
        public FatPressingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(buf);

            return this.factory.create(id, fluidInput);
        }

        @Override
        public void write(PacketByteBuf buf, FatPressingRecipe recipe)
        {
            recipe.fluidInput.write(buf);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends FatPressingRecipe>
        {
            T create(Identifier var1, RecipeInput<Fluid> in);
        }
    }
}
