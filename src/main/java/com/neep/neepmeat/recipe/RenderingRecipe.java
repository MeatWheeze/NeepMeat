package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.crucible.CrucibleStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
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
public class RenderingRecipe extends ImplementedRecipe<CrucibleStorage>
{
    protected Identifier id;
    protected RecipeInput<Item> itemInput;
    protected RecipeInput<Fluid> fluidInput;
    protected RecipeOutput<Fluid> fluidOutput;

    public RenderingRecipe(Identifier id, RecipeInput<Item> itemInput, RecipeInput<Fluid> fluidInput, RecipeOutput<Fluid> fluidOutput)
    {
        this.itemInput = itemInput;
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.id = id;
    }

    @Override
    public boolean matches(CrucibleStorage inventory, World world)
    {
        return itemInput.test(inventory.getItemStorage(null))
                && fluidInput.test(inventory.getStorage(null));
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    public RecipeInput<Item> getItemInput()
    {
        return itemInput;
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
        return NMrecipeTypes.RENDERING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.RENDERING;
    }

    public Item takeInputs(CrucibleStorage storage, int amount, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            Storage<ItemVariant> itemStorage = storage.getItemStorage(null);
            Storage<FluidVariant> fluidStorage = storage.getStorage(null);
            Optional<Item> item = itemInput.getFirstMatching(itemStorage, inner);
            Optional<Fluid> fluid = fluidInput.getFirstMatching(fluidStorage, inner);
            if (item.isEmpty() || fluid.isEmpty())
            {
                throw new IllegalStateException("Storage contents do not conform to recipe");
            }

            long ex1 = itemStorage.extract(ItemVariant.of(item.get()), itemInput.amount() * amount, inner);
            long ex2 = fluidStorage.extract(FluidVariant.of(fluid.get()), fluidInput.amount() * amount, inner);
            if (ex1 == itemInput.amount() * amount && ex2 == fluidInput.amount() * amount)
            {
                inner.commit();
                return item.get();
            }
            inner.abort();
        }
        return null;
    }

    public boolean ejectOutput(CrucibleStorage storage, int amount, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            fluidOutput.update();

            boolean bl1 = true;
            for (int i = 0; i < amount; ++i)
            {
                bl1 = bl1 && fluidOutput.insertInto(storage.getFluidOutput(), FluidVariant::of, inner);
            }

            if (bl1)
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer implements RecipeSerializer<RenderingRecipe>
    {
        RecipeFactory<RenderingRecipe> factory;

        public Serializer(RecipeFactory<RenderingRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public RenderingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject inputElement = JsonHelper.getObject(json, "item_input");
            RecipeInput<Item> itemInput = RecipeInput.fromJson(Registry.ITEM, inputElement);

            JsonObject fluidInputElement = JsonHelper.getObject(json, "fluid_input");
            RecipeInput<Fluid> fluidInput = RecipeInput.fromJson(Registry.FLUID, fluidInputElement);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromJson(Registry.FLUID, outputElement);
            if (outputElement.has("fat_item"))
            {
                if (!(fluidOutput.resource().equals(NMFluids.STILL_DIRTY_ORE_FAT)))
                    throw new IllegalStateException("Fluid '" + fluidOutput.resource() + "' is not an Ore Fat fluid");

                NbtCompound nbt = new NbtCompound();
                Identifier rawId = new Identifier(JsonHelper.getString(outputElement, "fat_item"));
                Registry.ITEM.getOrEmpty(rawId).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + rawId + "'"));
                nbt.putString("item", rawId.toString());
                fluidOutput.setNbt(nbt);
            }

            return this.factory.create(id, itemInput, fluidInput, fluidOutput);
        }

        @Override
        public RenderingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Item> itemInput = RecipeInput.fromBuffer(Registry.ITEM, buf);
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(Registry.FLUID, buf);
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromBuffer(Registry.FLUID, buf);

            return this.factory.create(id, itemInput, fluidInput, fluidOutput);
        }

        @Override
        public void write(PacketByteBuf buf, RenderingRecipe recipe)
        {
            recipe.itemInput.write(Registry.ITEM, buf);
            recipe.fluidInput.write(Registry.FLUID, buf);
            recipe.fluidOutput.write(Registry.FLUID, buf);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends RenderingRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> in, RecipeInput<Fluid> in2, RecipeOutput<Fluid> out);
        }
    }
}
