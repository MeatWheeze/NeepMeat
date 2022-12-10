package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.small_trommel.TrommelStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class TrommelRecipe extends ImplementedRecipe<TrommelStorage>
{
    protected Identifier id;
    protected RecipeInput<Fluid> fluidInput;
    protected NbtCompound inputNbt;
    protected RecipeOutput<Fluid> fluidOutput;

    public TrommelRecipe(Identifier id, RecipeInput<Fluid> fluidInput, NbtCompound inputNbt, RecipeOutput<Fluid> fluidOutput)
    {
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.id = id;
        this.inputNbt = inputNbt;
    }

    @Override
    public boolean matches(TrommelStorage inventory, World world)
    {
        WritableSingleFluidStorage storage = inventory.input();
        return fluidInput.test(storage) && fluidInput.amount() == storage.getAmount() && storage.getResource().nbtMatches(inputNbt);
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
    public RecipeSerializer<?> getSerializer()
    {
        return NMrecipeTypes.TROMMEL_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.TROMMEL;
    }

    public FluidVariant takeInputs(TrommelStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            Storage<FluidVariant> fluidStorage = storage.input();
            Optional<FluidVariant> fluid = fluidInput.getFirstMatching(fluidStorage, inputNbt, FluidVariant::of, inner);
            if (fluid.isEmpty())
            {
                throw new IllegalStateException("Storage contents do not conform to recipe");
            }

            long ex2 = fluidStorage.extract(fluid.get(), fluidInput.amount(), inner);
            if (ex2 == fluidInput.amount())
            {
                inner.commit();
                return fluid.get();
            }
            inner.abort();
        }
        return null;
    }

    public boolean ejectOutput(TrommelStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            fluidOutput.update();

            boolean bl1 = fluidOutput.insertInto(storage.output(), FluidVariant::of, inner);

            if (bl1)
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer implements RecipeSerializer<TrommelRecipe>
    {
        RecipeFactory<TrommelRecipe> factory;

        public Serializer(RecipeFactory<TrommelRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public TrommelRecipe read(Identifier id, JsonObject json)
        {
            JsonObject fluidInputElement = JsonHelper.getObject(json, "fluid_input");
            RecipeInput<Fluid> fluidInput = RecipeInput.fromJsonRegistry(RecipeInputs.FLUID, fluidInputElement);

            NbtCompound inputNbt = null;
            if (fluidInputElement.has("fat_item"))
            {
                Identifier rawId = new Identifier(JsonHelper.getString(fluidInputElement, "fat_item"));
                Registry.ITEM.getOrEmpty(rawId).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + rawId + "'"));
                inputNbt = new NbtCompound();
                inputNbt.putString("item", rawId.toString());
            }

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromJsonRegistry(Registry.FLUID, outputElement);
            if (outputElement.has("fat_item"))
            {
                if (!(fluidOutput.resource().equals(NMFluids.STILL_CLEAN_ORE_FAT)))
                    throw new IllegalStateException("Fluid '" + fluidOutput.resource() + "' is not an Ore Fat fluid");

                NbtCompound outputNbt = new NbtCompound();
                Identifier rawId = new Identifier(JsonHelper.getString(outputElement, "fat_item"));
                Registry.ITEM.getOrEmpty(rawId).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + rawId + "'"));
                outputNbt.putString("item", rawId.toString());
                fluidOutput.setNbt(outputNbt);
            }

            return this.factory.create(id, fluidInput, inputNbt, fluidOutput);
        }

        @Override
        public TrommelRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(buf);
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromBuffer(Registry.FLUID, buf);
            NbtCompound nbt = buf.readNbt();

            return this.factory.create(id, fluidInput, nbt, fluidOutput);
        }

        @Override
        public void write(PacketByteBuf buf, TrommelRecipe recipe)
        {
            recipe.fluidInput.write(buf);
            recipe.fluidOutput.write(Registry.FLUID, buf);
            buf.writeNbt(recipe.inputNbt);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends TrommelRecipe>
        {
            T create(Identifier var1, RecipeInput<Fluid> in, NbtCompound nbt, RecipeOutput<Fluid> out);
        }
    }
}
