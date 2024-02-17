package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.MeatlibRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.alloy_kiln.AlloyKilnStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class AlloyKilnRecipe implements MeatlibRecipe<AlloyKilnStorage>
{
    protected Identifier id;
    protected RecipeInput<Item> itemInput1;
    protected RecipeInput<Item> itemInput2;
    protected RecipeOutputImpl<Item> itemOutput;
    protected int processTime;

    public AlloyKilnRecipe(Identifier id, RecipeInput<Item> itemInput1, RecipeInput<Item> itemInput2, RecipeOutputImpl<Item> itemOutput, int processTime)
    {
        this.itemInput1 = itemInput1;
        this.itemInput2 = itemInput2;
        this.itemOutput = itemOutput;
        this.processTime = processTime;
        this.id = id;
    }

    @Override
    public boolean matches(AlloyKilnStorage context)
    {
        List<SingleSlotStorage<ItemVariant>> slots = List.of(
                context.getSlot(AlloyKilnStorage.INPUT_1),
                context.getSlot(AlloyKilnStorage.INPUT_2));

        List<RecipeInput<Item>> queue = new LinkedList<>(List.of(itemInput1, itemInput2));

        ListIterator<RecipeInput<Item>> it = queue.listIterator();
        while (it.hasNext())
        {
            RecipeInput<Item> ingredient = it.next();
            for (StorageView<ItemVariant> view : slots)
            {
                if (ingredient.test(view))
                {
                    it.remove();
                    break;
                }
            }
        }
        return queue.size() == 0;
    }

    public RecipeInput<Item> getItemInput1()
    {
        return itemInput1;
    }

    public RecipeInput<Item> getItemInput2()
    {
        return itemInput2;
    }

    public RecipeOutputImpl<Item> getItemOutput()
    {
        return itemOutput;
    }

    public int getProcessTime()
    {
        return processTime;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.ALLOY_SMELTING;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.ALLOY_KILN_SERIALIZER;
    }

    public int getTime()
    {
        return processTime;
    }

    public boolean takeInputs(AlloyKilnStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            Storage<ItemVariant> inputStorage = storage.getInputStorage();

            Optional<Item> item1 = itemInput1.getFirstMatching(inputStorage, inner);
            Optional<Item> item2 = itemInput2.getFirstMatching(inputStorage, inner);

            if (item1.isEmpty() || item2.isEmpty())
            {
                throw new IllegalStateException("Storage contents must conform to recipe");
            }

            long extracted1 = inputStorage.extract(ItemVariant.of(item1.get()), itemInput1.amount(), transaction);
            long extracted2 = inputStorage.extract(ItemVariant.of(item2.get()), itemInput2.amount(), transaction);
            if (extracted1 == itemInput1.amount() && extracted2 == itemInput2.amount())
            {
                inner.commit();
                storage.markDirty();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    @Override
    public boolean ejectOutputs(AlloyKilnStorage context, TransactionContext transaction)
    {
        return false;
    }

    public boolean ejectOutput(AlloyKilnStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            itemOutput.update();

            boolean bl1 = itemOutput.insertInto(storage.getOutputStorage(), ItemVariant::of, inner);

            if (bl1)
            {
                inner.commit();
                storage.markDirty();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer implements MeatRecipeSerialiser<AlloyKilnRecipe>
    {
        RecipeFactory<AlloyKilnRecipe> factory;
        int processTIme;

        public Serializer(RecipeFactory<AlloyKilnRecipe> recipeFactory, int processTime)
        {
            this.factory = recipeFactory;
            this.processTIme = processTime;
        }

        @Override
        public AlloyKilnRecipe read(Identifier id, JsonObject json)
        {
            JsonObject inputElement1 = JsonHelper.getObject(json, "input1");
            RecipeInput<Item> itemInput1 = RecipeInput.fromJsonRegistry(RecipeInputs.ITEM, inputElement1);

            JsonObject inputElement2 = JsonHelper.getObject(json, "input2");
            RecipeInput<Item> itemInput2 = RecipeInput.fromJsonRegistry(RecipeInputs.ITEM, inputElement2);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutputImpl<Item> itemOutput = RecipeOutputImpl.fromJsonRegistry(Registry.ITEM, outputElement);

            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput1, itemInput2, itemOutput, time);
        }

        @Override
        public AlloyKilnRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Item> itemInput1 = RecipeInput.fromBuffer(buf);
            RecipeInput<Item> itemInput2 = RecipeInput.fromBuffer(buf);
            RecipeOutputImpl<Item> itemOutput = RecipeOutputImpl.fromBuffer(Registry.ITEM, buf);

            int time = buf.readVarInt();

            return this.factory.create(id, itemInput1, itemInput2, itemOutput, time);
        }

        @Override
        public void write(PacketByteBuf buf, AlloyKilnRecipe recipe)
        {
            recipe.itemInput1.write(buf);
            recipe.itemInput2.write(buf);
            recipe.itemOutput.write(Registry.ITEM, buf);

            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends AlloyKilnRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> in1, RecipeInput<Item> in2, RecipeOutputImpl<Item> out, int time);
        }
    }
}
