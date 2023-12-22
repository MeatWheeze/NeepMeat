package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.RecipeInput;
import com.neep.meatlib.recipe.RecipeOutput;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.casting_basin.CastingBasinStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class PressingRecipe extends ImplementedRecipe<CastingBasinStorage>
{
    protected Identifier id;
    protected RecipeInput<Fluid> fluidInput;

    public PressingRecipe(Identifier id, RecipeInput<Fluid> fluidInput)
    {
        this.fluidInput = fluidInput;
        this.id = id;
    }

    @Override
    public boolean matches(CastingBasinStorage inventory, World world)
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
    public RecipeSerializer<?> getSerializer()
    {
        return NMrecipeTypes.PRESSING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.PRESSING;
    }

    public FluidVariant takeInputs(CastingBasinStorage storage, TransactionContext transaction)
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
                return null;
            }

            // Might need to formalise this
            long insertAmount = 1;
            long transferred = storage.item(null).insert(ItemVariant.of(entry.result()), insertAmount, inner);

            if (transferred == insertAmount)
            {
                inner.commit();
                return fluidVariant;
            }
            inner.abort();
        }
        return null;
    }

    public static class Serializer implements RecipeSerializer<PressingRecipe>
    {
        RecipeFactory<PressingRecipe> factory;

        public Serializer(RecipeFactory<PressingRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public PressingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject fluidInputElement = JsonHelper.getObject(json, "fluid_input");
            RecipeInput<Fluid> fluidInput = RecipeInput.fromJson(Registry.FLUID, fluidInputElement);

            return this.factory.create(id, fluidInput);
        }

        @Override
        public PressingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(Registry.FLUID, buf);

            return this.factory.create(id, fluidInput);
        }

        @Override
        public void write(PacketByteBuf buf, PressingRecipe recipe)
        {
            recipe.fluidInput.write(Registry.FLUID, buf);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends PressingRecipe>
        {
            T create(Identifier var1, RecipeInput<Fluid> in);
        }
    }
}
