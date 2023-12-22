package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.RecipeInput;
import com.neep.meatlib.recipe.RecipeOutput;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.alloy_kiln.AlloyKilnStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class AlloyKilnRecipe implements Recipe<AlloyKilnStorage>
{
    protected Identifier id;
    protected RecipeInput<Item> itemInput1;
    protected RecipeInput<Item> itemInput2;
    protected RecipeOutput<Item> itemOutput;
    protected int processTime;

    public AlloyKilnRecipe(Identifier id, RecipeInput<Item> itemInput1, RecipeInput<Item> itemInput2, RecipeOutput<Item> itemOutput, int processTime)
    {
        this.itemInput1 = itemInput1;
        this.itemInput2 = itemInput2;
        this.itemOutput = itemOutput;
        this.processTime = processTime;
        this.id = id;
    }

    @Override
    public boolean matches(AlloyKilnStorage inventory, World world)
    {
        List<SingleSlotStorage<ItemVariant>> slots = List.of(
                inventory.getSlot(AlloyKilnStorage.INPUT_1),
                inventory.getSlot(AlloyKilnStorage.INPUT_2));

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

    @Override
    public ItemStack craft(AlloyKilnStorage inventory)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getOutput()
    {
        throw new UnsupportedOperationException("use getItemOutput instead");
    }

    public RecipeInput<Item> getItemInput1()
    {
        return itemInput1;
    }

    public RecipeInput<Item> getItemInput2()
    {
        return itemInput2;
    }

    public RecipeOutput<Item> getItemOutput()
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
    public RecipeSerializer<?> getSerializer()
    {
        return NMrecipeTypes.ALLOY_KILN_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.ALLOY_SMELTING;
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

    public static class Serializer implements RecipeSerializer<AlloyKilnRecipe>
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
            RecipeInput<Item> itemInput1 = RecipeInput.fromJson(Registry.ITEM, inputElement1);

            JsonObject inputElement2 = JsonHelper.getObject(json, "input2");
            RecipeInput<Item> itemInput2 = RecipeInput.fromJson(Registry.ITEM, inputElement2);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Item> itemOutput = RecipeOutput.fromJson(Registry.ITEM, outputElement);

            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput1, itemInput2, itemOutput, time);
        }

        @Override
        public AlloyKilnRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Item> itemInput1 = RecipeInput.fromBuffer(Registry.ITEM, buf);
            RecipeInput<Item> itemInput2 = RecipeInput.fromBuffer(Registry.ITEM, buf);
            RecipeOutput<Item> itemOutput = RecipeOutput.fromBuffer(Registry.ITEM, buf);

            int time = buf.readVarInt();

            return this.factory.create(id, itemInput1, itemInput2, itemOutput, time);
        }

        @Override
        public void write(PacketByteBuf buf, AlloyKilnRecipe recipe)
        {
            recipe.itemInput1.write(Registry.ITEM, buf);
            recipe.itemInput2.write(Registry.ITEM, buf);
            recipe.itemOutput.write(Registry.ITEM, buf);

            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends AlloyKilnRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> in1, RecipeInput<Item> in2, RecipeOutput<Item> out, int time);
        }
    }
}
