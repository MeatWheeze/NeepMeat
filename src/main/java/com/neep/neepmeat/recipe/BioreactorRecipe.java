package com.neep.neepmeat.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neep.meatlib.network.PacketBufUtil;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatlibRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.live_machine.process.BioreactorProcess;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;

public class BioreactorRecipe implements MeatlibRecipe<BioreactorProcess.Context>
{
    private final Identifier id;
    private final List<RecipeInput<Item>> itemInputs;
    private final List<RecipeInput<Fluid>> fluidInputs;
    private final RecipeOutput<Fluid> output;
    private final int processTime;

    public BioreactorRecipe(Identifier id, List<RecipeInput<Item>> itemInputs, List<RecipeInput<Fluid>> fluidInputs, RecipeOutput<Fluid> output, int processTime)
    {
        this.id = id;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.output = output;
        this.processTime = processTime;
    }

    @Override
    public boolean matches(BioreactorProcess.Context context)
    {
        int satisfied = itemInputs.size();
        for (var input : itemInputs)
        {
            if (input.testStorage(context.itemInput()))
                satisfied--;
        }
        for (var input : fluidInputs)
        {
            if (input.testStorage(context.fluidInput()))
                satisfied--;
        }

        return satisfied <= 0;
    }

    @Override
    public boolean takeInputs(BioreactorProcess.Context context, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            for (var input : itemInputs)
            {
                if (!input.extract(context.itemInput(), ItemVariant::of, inner))
                    return false;
            }

            for (var input : fluidInputs)
            {
                if (!input.extract(context.fluidInput(), FluidVariant::of, inner))
                    return false;
            }

            inner.commit();
            return true;
        }
    }

    @Override
    public boolean ejectOutputs(BioreactorProcess.Context context, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            if (output.insertInto(context.fluidOutput(), FluidVariant::of, inner))
            {
                inner.commit();
                return true;
            }
        }
        return false;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.BIOREACTOR_SERIALISER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.BIOREACTOR;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    public int getProcessTime()
    {
        return processTime;
    }

    public static class Serialiser implements MeatRecipeSerialiser<BioreactorRecipe>
    {
        @Override
        public BioreactorRecipe read(Identifier id, JsonObject json)
        {
            JsonArray array = json.getAsJsonArray("inputs");

            List<RecipeInput<Item>> itemInputs = new ArrayList<>();
            List<RecipeInput<Fluid>> fluidInputs = new ArrayList<>();
            for (int i = 0; i < array.size(); ++i)
            {
                JsonObject object = (JsonObject) array.get(i);

                // JAAAAAAAANK
                RecipeInput<?> recipeInput = RecipeInput.fromJson(object);
                if (recipeInput.getType() == RecipeInputs.ITEM_ID)
                    itemInputs.add((RecipeInput<Item>) recipeInput);
                else if (recipeInput.getType() == RecipeInputs.FLUID_ID)
                    fluidInputs.add((RecipeInput<Fluid>) recipeInput);
            }

            RecipeOutput<Fluid> recipeOutput = RecipeOutputImpl.fromJsonRegistry(Registries.FLUID, JsonHelper.getObject(json, "output"));

            int processTime = JsonHelper.getInt(json, "process_time");

            return new BioreactorRecipe(id, itemInputs, fluidInputs, recipeOutput, processTime);
        }

        @Override
        public BioreactorRecipe read(Identifier id, PacketByteBuf buf)
        {
            List<RecipeInput<Item>> itemInputs = PacketBufUtil.readList(buf, RecipeInput::fromBuffer);
            List<RecipeInput<Fluid>> fluidInputs = PacketBufUtil.readList(buf, RecipeInput::fromBuffer);

            RecipeOutput<Fluid> output = RecipeOutputImpl.fromBuffer(Registries.FLUID, buf);

            int processTime = buf.readVarInt();

            return new BioreactorRecipe(id, itemInputs, fluidInputs, output, processTime);
        }

        @Override
        public void write(PacketByteBuf buf, BioreactorRecipe recipe)
        {
            PacketBufUtil.writeList(buf, recipe.fluidInputs, (b, i) -> i.write(b));
            PacketBufUtil.writeList(buf, recipe.itemInputs, (b, i) -> i.write(b));

            recipe.output.write(Registries.FLUID, buf);

            buf.writeVarInt(recipe.processTime);
        }
    }
}
