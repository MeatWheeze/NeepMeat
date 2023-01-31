package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.small_trommel.TrommelStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class TrommelRecipe implements MeatRecipe<TrommelStorage>
{
    protected final Identifier id;
    protected final RecipeInput<Fluid> fluidInput;
    protected final RecipeOutput<Fluid> fluidOutput;
    protected final RecipeOutput<Item> itemOutput;

    public TrommelRecipe(Identifier id, RecipeInput<Fluid> fluidInput, RecipeOutput<Fluid> fluidOutput, RecipeOutput<Item> itemOutput)
    {
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.itemOutput = itemOutput;
        this.id = id;
    }

    @Override
    public boolean matches(TrommelStorage inventory)
    {
        WritableSingleFluidStorage storage = inventory.input();
        return fluidInput.test(storage) && fluidInput.amount() <= storage.getAmount();
    }

    public RecipeInput<Fluid> getFluidInput()
    {
        return fluidInput;
    }

    public RecipeOutput<Fluid> getFluidOutput()
    {
        return fluidOutput;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.TROMMEL;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return NMrecipeTypes.TROMMEL_SERIALIZER;
    }

    @Override
    public boolean takeInputs(TrommelStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            Storage<FluidVariant> fluidStorage = storage.input();
            Optional<Fluid> fluid = fluidInput.getFirstMatching(fluidStorage, inner);
            if (fluid.isEmpty())
            {
                throw new IllegalStateException("Storage contents do not conform to recipe");
            }

            long ex2 = fluidStorage.extract(FluidVariant.of(fluid.get()), fluidInput.amount(), inner);
            if (ex2 == fluidInput.amount())
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    @Override
    public boolean ejectOutputs(TrommelStorage context, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            fluidOutput.update();
            itemOutput.update();

            boolean bl1 = fluidOutput.insertInto(context.output(), FluidVariant::of, inner);
            boolean bl2 = itemOutput.insertInto(context.itemOutput(), ItemVariant::of, inner);

            if (bl1)
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer implements MeatRecipeSerialiser<TrommelRecipe>
    {
        RecipeFactory<TrommelRecipe> factory;

        public Serializer(RecipeFactory<TrommelRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public TrommelRecipe read(Identifier id, JsonObject json)
        {
            JsonObject fluidInputElement = JsonHelper.getObject(json, "input");
            RecipeInput<Fluid> fluidInput = RecipeInput.fromJsonRegistry(RecipeInputs.FLUID, fluidInputElement);

            JsonObject fluidOutputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromJsonRegistry(Registry.FLUID, fluidOutputElement);

            JsonObject itemOutputElement = JsonHelper.getObject(json, "aux_output");
            RecipeOutput<Item> itemOutput = RecipeOutput.fromJsonRegistry(Registry.ITEM, itemOutputElement);

            return this.factory.create(id, fluidInput, fluidOutput, itemOutput);
        }

        @Override
        public TrommelRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(buf);
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromBuffer(Registry.FLUID, buf);
            RecipeOutput<Item> itemOutput = RecipeOutput.fromBuffer(Registry.ITEM, buf);

            return this.factory.create(id, fluidInput, fluidOutput, itemOutput);
        }

        @Override
        public void write(PacketByteBuf buf, TrommelRecipe recipe)
        {
            recipe.fluidInput.write(buf);
            recipe.fluidOutput.write(Registry.FLUID, buf);
            recipe.itemOutput.write(Registry.ITEM, buf);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends TrommelRecipe>
        {
            T create(Identifier id, RecipeInput<Fluid> in, RecipeOutput<Fluid> out, RecipeOutput<Item> itemOut);
        }
    }
}
