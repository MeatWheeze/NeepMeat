package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.multitank.MultiTankBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class FluidHeatingRecipe implements MeatRecipe<MultiTankBlockEntity>
{
    protected Identifier id;
    protected RecipeInput<Fluid> fluidInput;
    protected RecipeOutputImpl<Fluid> fluidOutput;
    protected int processTime;

    public FluidHeatingRecipe(Identifier id, RecipeInput<Fluid> fluidInput, RecipeOutputImpl<Fluid> fluidOutput, int processTime)
    {
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.processTime = processTime;
        this.id = id;
    }

    @Override
    public boolean matches(MultiTankBlockEntity inventory)
    {
        for (Map.Entry<FluidVariant, Long> entry : inventory.getStorage().getSlots())
        {
            if (fluidInput.test(entry.getKey(), entry.getValue()))
            {
                return true;
            }
        }
        return false;
    }

    public RecipeInput<Fluid> getFluidInput()
    {
        return fluidInput;
    }

    public RecipeOutputImpl<Fluid> getFluidOutput()
    {
        return fluidOutput;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return NMrecipeTypes.HEATING_SERIALISER;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.HEATING;
    }

    public int getTime()
    {
        return processTime;
    }

    @Override
    public boolean takeInputs(MultiTankBlockEntity storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            Optional<Fluid> fluid = fluidInput.getFirstMatching(storage.getStorage(), transaction);
            if (!fluid.isPresent())
            {
                return false;
            }

            long extracted = storage.getStorage().extract(FluidVariant.of(fluid.get()), fluidInput.amount(), transaction);
            if (extracted == fluidInput.amount())
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    @Override
    public boolean ejectOutputs(MultiTankBlockEntity context, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            fluidOutput.update();

            boolean bl1 = fluidOutput.insertInto(context.getStorage(), FluidVariant::of, inner);

            if (bl1)
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer implements MeatRecipeSerialiser<FluidHeatingRecipe>
    {
        RecipeFactory<FluidHeatingRecipe> factory;
        int processTIme;

        public Serializer(RecipeFactory<FluidHeatingRecipe> recipeFactory, int processTime)
        {
            this.factory = recipeFactory;
            this.processTIme = processTime;
        }

        @Override
        public FluidHeatingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject inputElement = JsonHelper.getObject(json, "input");
            RecipeInput<Fluid> itemInput = RecipeInput.fromJsonRegistry(RecipeInputs.FLUID, inputElement);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutputImpl<Fluid> itemOutput = RecipeOutputImpl.fromJsonRegistry(Registry.FLUID, outputElement);

            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput, itemOutput, time);
        }

        @Override
        public FluidHeatingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(buf);
            RecipeOutputImpl<Fluid> fluidOutput = RecipeOutputImpl.fromBuffer(Registry.FLUID, buf);

            int time = buf.readVarInt();

            return this.factory.create(id, fluidInput, fluidOutput, time);
        }

        @Override
        public void write(PacketByteBuf buf, FluidHeatingRecipe recipe)
        {
            recipe.fluidInput.write(buf);
            recipe.fluidOutput.write(Registry.FLUID, buf);

            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends FluidHeatingRecipe>
        {
            T create(Identifier var1, RecipeInput<Fluid> in, RecipeOutputImpl<Fluid> out, int time);
        }
    }
}
