package com.neep.neepmeat.machine.mixer;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMrecipeTypes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

@SuppressWarnings("UnstableApiUsage")
public class MixingRecipe extends ImplementedRecipe<MixerStorage>
{
    protected Identifier id;
    protected RecipeInput<Item> itemInput;
    protected RecipeInput<Fluid> fluidInput1;
    protected RecipeInput<Fluid> fluidInput2;
    protected RecipeOutputImpl<Fluid> fluidOutput;
    protected int processTime;

    public MixingRecipe(Identifier id, RecipeInput<Item> itemInput, RecipeInput<Fluid> fluidInput1, RecipeInput<Fluid> fluidInput2, RecipeOutputImpl<Fluid> fluidOutput, int processTime)
    {
        this.itemInput = itemInput;
        this.fluidInput1 = fluidInput1;
        this.fluidInput2 = fluidInput2;
        this.fluidOutput = fluidOutput;
        this.processTime = processTime;
        this.id = id;
    }

    public RecipeInput<Item> getItemIngredient()
    {
        return itemInput;
    }

    public List<RecipeInput<Fluid>> getFluidInputs()
    {
        return List.of(fluidInput1, fluidInput2);
    }

    public RecipeOutputImpl<Fluid> getFluidOutput()
    {
        return fluidOutput;
    }

    public int getProcessTime()
    {
        return processTime;
    }

    @Override
    public boolean matches(MixerStorage inventory, World world)
    {
        Transaction transaction = Transaction.openOuter();
        List<RecipeInput<Fluid>> queue = new LinkedList<>(List.of(fluidInput1, fluidInput2));

        ListIterator<RecipeInput<Fluid>> it = queue.listIterator();
        var parentStorages = inventory.getInputStorages();
        while (it.hasNext())
        {
            RecipeInput<Fluid> ingredient = it.next();
            if (ingredient.isEmpty())
            {
                it.remove();
                continue;
            }
            for (StorageView<FluidVariant> view : parentStorages)
            {
                if (ingredient.test(view))
                {
                    it.remove();
                    break;
                }
            }
        }
        transaction.abort();
        return queue.size() == 0 && itemInput.test(inventory.getItemInput());
    }

    public boolean takeInputs(MixerStorage inventory, TransactionContext transactionContext)
    {
        try (Transaction inner = transactionContext.openNested())
        {
            List<Storage<FluidVariant>> inputList = inventory.parent.getAdjacentStorages();
            Storage<FluidVariant> fluidStorage = inventory.getInputStorages();

            if (!inputList.isEmpty())
            {
                Fluid fluid1 = fluidInput1.isEmpty() ? Fluids.EMPTY : fluidInput1.getFirstMatching(fluidStorage, transactionContext).orElseThrow(() -> new IllegalStateException("Storage contents must conform to recipe"));
                Fluid fluid2 = fluidInput2.isEmpty() ? Fluids.EMPTY : fluidInput2.getFirstMatching(fluidStorage, transactionContext).orElseThrow(() -> new IllegalStateException("Storage contents must conform to recipe"));
                Item item = itemInput.isEmpty() ? Items.DIRT : itemInput.getFirstMatching(inventory.getItemInput()).orElseThrow(() -> new IllegalStateException("Storage contents must conform to recipe"));

                FluidVariant variant1 = FluidVariant.of(fluid1);
                FluidVariant variant2 = FluidVariant.of(fluid2);

                long ext1 = fluidStorage.extract(variant1, fluidInput1.amount(), inner);
                // Do not attempt to extract blank variant
                long ext2 = variant2.isBlank() ? fluidInput2.amount() : fluidStorage.extract(variant2, fluidInput2.amount(), inner);
                long ext3 = itemInput.isEmpty() ? itemInput.amount() : inventory.getItemInput().extract(ItemVariant.of(item), itemInput.amount(), inner);

                if (ext1 == fluidInput1.amount() && ext2 == fluidInput2.amount() && ext3 == itemInput.amount())
                {
                    inner.commit();
                    inventory.displayInput1 = variant1;
                    inventory.displayInput2 = variant2;
                    return true;
                }
            }
            inner.abort();
        }
        return false;
    }

    public boolean ejectOutput(MixerStorage inventory, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            Storage<FluidVariant> output = inventory.getFluidOutput();
            if (output != null)
            {
                if (fluidOutput.insertInto(output, FluidVariant::of, transaction))
                {
                    inner.commit();
                    return true;
                }
            }
            inner.abort();
        }
        return false;
    }
    
    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return NMrecipeTypes.MIXING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.MIXING;
    }

    public static class MixerSerializer implements RecipeSerializer<MixingRecipe>
    {
        RecipeFactory<MixingRecipe> factory;
        int processTIme;

        public MixerSerializer(RecipeFactory<MixingRecipe> recipeFactory, int processTime)
        {
            this.factory = recipeFactory;
            this.processTIme = processTime;
        }

        @Override
        public MixingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject itemElement = JsonHelper.getObject(json, "item");
            RecipeInput<Item> itemInput = RecipeInput.fromJsonRegistry(RecipeInputs.ITEM, itemElement);

            JsonObject fluidElement1 = JsonHelper.getObject(json, "fluid1");
            RecipeInput<Fluid> fluidInput1 = RecipeInput.fromJsonRegistry(RecipeInputs.FLUID, fluidElement1);

            JsonObject fluidElement2 = JsonHelper.getObject(json, "fluid2");
            RecipeInput<Fluid> fluidInput2 = RecipeInput.fromJsonRegistry(RecipeInputs.FLUID, fluidElement2);

            JsonObject fluidElement3 = JsonHelper.getObject(json, "output");
            RecipeOutputImpl<Fluid> fluidOutput = RecipeOutputImpl.fromJsonRegistry(Registry.FLUID, fluidElement3);

            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput, fluidInput1, fluidInput2, fluidOutput, time);
        }

        @Override
        public MixingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Item> ingredient = RecipeInput.fromBuffer(buf);
            RecipeInput<Fluid> fluidInput1 = RecipeInput.fromBuffer(buf);
            RecipeInput<Fluid> fluidInput2 = RecipeInput.fromBuffer(buf);
            RecipeOutputImpl<Fluid> fluidOutput = RecipeOutputImpl.fromBuffer(Registry.FLUID, buf);
            int time = buf.readVarInt();

            return this.factory.create(id, ingredient, fluidInput1, fluidInput2, fluidOutput, time);
        }

        @Override
        public void write(PacketByteBuf buf, MixingRecipe recipe)
        {
            recipe.itemInput.write(buf);
            recipe.fluidInput1.write(buf);
            recipe.fluidInput2.write(buf);
            recipe.fluidOutput.write(Registry.FLUID, buf);
            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends MixingRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> var3, RecipeInput<Fluid> f1, RecipeInput<Fluid> f2, RecipeOutputImpl<Fluid> out, int time);
        }
    }

}
