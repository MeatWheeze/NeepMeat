package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.RecipeInput;
import com.neep.meatlib.recipe.RecipeOutput;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.casting_basin.CastingBasinStorage;
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
public class PressingRecipe extends ImplementedRecipe<CastingBasinStorage>
{
    protected Identifier id;
    protected RecipeInput<Fluid> fluidInput;
    protected NbtCompound inputNbt;
    protected RecipeOutput<Item> itemOutput;

    public PressingRecipe(Identifier id, RecipeInput<Fluid> fluidInput, NbtCompound inputNbt, RecipeOutput<Item> itemOutput)
    {
        this.fluidInput = fluidInput;
        this.itemOutput = itemOutput;
        this.id = id;
        this.inputNbt = inputNbt;
    }

    @Override
    public boolean matches(CastingBasinStorage inventory, World world)
    {
        WritableSingleFluidStorage storage = inventory.fluid(null);
        return fluidInput.test(storage) && fluidInput.amount() == storage.getAmount() && storage.getResource().nbtMatches(inputNbt);
    }

    public RecipeInput<Fluid> getFluidInput()
    {
        return fluidInput;
    }

    public RecipeOutput<Item> getItemOutput()
    {
        return itemOutput;
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
            Storage<FluidVariant> fluidStorage = storage.fluid(null);
            Optional<FluidVariant> fluid = fluidInput.getFirstMatching(fluidStorage, inputNbt, FluidVariant::of, inner);
            if (fluid.isEmpty())
            {
                throw new IllegalStateException("Storage contents do not conform to recipe");
            }

            storage.unlock();
            long ex2 = fluidStorage.extract(fluid.get(), fluidInput.amount(), inner);
            storage.lock();
            if (ex2 == fluidInput.amount())
            {
                inner.commit();
                return fluid.get();
            }
            inner.abort();
        }
        return null;
    }

    public boolean ejectOutput(CastingBasinStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            itemOutput.update();

            boolean bl1 = itemOutput.insertInto(storage.item(null), ItemVariant::of, inner);

            if (bl1)
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
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

            NbtCompound nbt = new NbtCompound();
            if (fluidInputElement.has("fat_item"))
            {
                Identifier rawId = new Identifier(JsonHelper.getString(fluidInputElement, "fat_item"));
                Registry.ITEM.getOrEmpty(rawId).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + rawId + "'"));
                nbt.putString("item", rawId.toString());
            }

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Item> itemOutput = RecipeOutput.fromJson(Registry.ITEM, outputElement);

            return this.factory.create(id, fluidInput, nbt, itemOutput);
        }

        @Override
        public PressingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(Registry.FLUID, buf);
            RecipeOutput<Item> itemOutput = RecipeOutput.fromBuffer(Registry.ITEM, buf);
            NbtCompound nbt = buf.readNbt();

            return this.factory.create(id, fluidInput, nbt, itemOutput);
        }

        @Override
        public void write(PacketByteBuf buf, PressingRecipe recipe)
        {
            recipe.fluidInput.write(Registry.FLUID, buf);
            recipe.itemOutput.write(Registry.ITEM, buf);
            buf.writeNbt(recipe.inputNbt);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends PressingRecipe>
        {
            T create(Identifier var1, RecipeInput<Fluid> in, NbtCompound nbt, RecipeOutput<Item> out);
        }
    }
}
