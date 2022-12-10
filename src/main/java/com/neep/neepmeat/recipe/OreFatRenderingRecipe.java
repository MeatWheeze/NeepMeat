package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.crucible.CrucibleStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
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

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class OreFatRenderingRecipe extends ImplementedRecipe<CrucibleStorage>
{
    protected Identifier id;
    protected RecipeInput<Fluid> fluidInput;
    protected RecipeOutput<Fluid> fluidOutput;

    public OreFatRenderingRecipe(Identifier id, RecipeInput<Fluid> fluidInput, RecipeOutput<Fluid> fluidOutput)
    {
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.id = id;
    }

    @Override
    public boolean matches(CrucibleStorage inventory, World world)
    {
        return fluidInput.test(inventory.getStorage(null))
                && OreFatRegistry.getFromInput(inventory.getItemStorage(null).getResource().getItem()) != null;
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
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
        return NMrecipeTypes.ORE_FAT_RENDERING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.ORE_FAT_RENDERING;
    }

    public Item takeInputs(CrucibleStorage storage, int amount, TransactionContext transaction)
    {
        SingleSlotStorage<ItemVariant> itemStorage = storage.getItemStorage(null);
        Storage<FluidVariant> fluidStorage = storage.getStorage(null);
        OreFatRegistry.Entry entry = OreFatRegistry.getFromInput(itemStorage.getResource().getItem());
        Item item = itemStorage.getResource().getItem();

        try (Transaction take = transaction.openNested())
        {
            Optional<Fluid> fluid = fluidInput.getFirstMatching(fluidStorage, take);
            if (entry == null || fluid.isEmpty())
            {
                throw new IllegalStateException("Storage contents do not conform to recipe");
            }

            long ex1 = itemStorage.extract(ItemVariant.of(item), amount, take);
            long ex2 = fluidStorage.extract(FluidVariant.of(fluid.get()), fluidInput.amount() * amount, take);
            if (ex1 == amount && ex2 == fluidInput.amount() * amount)
            {
                take.commit();
            }
            else take.abort();
        }

        try (Transaction eject = transaction.openNested())
        {
            fluidOutput.update();

            boolean bl1 = true;
            fluidOutput.setNbt(entry.toNbt());
            for (int i = 0; i < amount; ++i)
            {
                bl1 = bl1 && fluidOutput.insertInto(storage.getFluidOutput(), FluidVariant::of, eject);
            }
            fluidOutput.setNbt(null);

            if (bl1)
            {
                eject.commit();
                return item;
            }
            else eject.abort();
        }
        return null;
    }

    public static class Serializer implements RecipeSerializer<OreFatRenderingRecipe>
    {
        RecipeFactory<OreFatRenderingRecipe> factory;

        public Serializer(RecipeFactory<OreFatRenderingRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public OreFatRenderingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject fluidInputElement = JsonHelper.getObject(json, "fluid_input");
            RecipeInput<Fluid> fluidInput = RecipeInput.fromJsonRegistry(RecipeInputs.FLUID, fluidInputElement);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromJsonRegistry(Registry.FLUID, outputElement);

            return this.factory.create(id, fluidInput, fluidOutput);
        }

        @Override
        public OreFatRenderingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(RecipeInputs.FLUID, buf);
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromBuffer(Registry.FLUID, buf);

            return this.factory.create(id, fluidInput, fluidOutput);
        }

        @Override
        public void write(PacketByteBuf buf, OreFatRenderingRecipe recipe)
        {
            recipe.fluidInput.write(buf);
            recipe.fluidOutput.write(Registry.FLUID, buf);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends OreFatRenderingRecipe>
        {
            T create(Identifier var1, RecipeInput<Fluid> in2, RecipeOutput<Fluid> out);
        }
    }
}
