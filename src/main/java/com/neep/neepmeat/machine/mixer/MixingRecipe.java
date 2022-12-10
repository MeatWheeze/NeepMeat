package com.neep.neepmeat.machine.mixer;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.ingredient.FluidIngredient;
import com.neep.meatlib.recipe.ingredient.GenericIngredient;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.neepmeat.init.NMrecipeTypes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
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

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class MixingRecipe implements Recipe<MixerStorage>
{
    protected Identifier id;
    protected RecipeInput<Item> itemInput;
    protected FluidIngredient fluidInput1;
    protected FluidIngredient fluidInput2;
    protected FluidIngredient fluidOutput;
    protected int processTime;

    public MixingRecipe(Identifier id, RecipeInput<Item> itemInput, FluidIngredient fluidInput1, FluidIngredient fluidInput2, FluidIngredient fluidOutput, int processTime)
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

    public List<FluidIngredient> getFluidInputs()
    {
        return List.of(fluidInput1, fluidInput2);
    }

    public FluidIngredient getFluidOutput()
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
        List<GenericIngredient<?, ?>> queue = new LinkedList<>(List.of(fluidInput1, fluidInput2));

        ListIterator<GenericIngredient<?, ?>> it = queue.listIterator();
        while (it.hasNext())
        {
            GenericIngredient<?, ?> ingredient = it.next();
            for (StorageView<?> view : inventory.getInputStorages(transaction))
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
            CombinedStorage<FluidVariant, Storage<FluidVariant>> fluidStorage = new CombinedStorage<>(inputList);
            Storage<ItemVariant> itemStorage = inventory.parent.getItemStorage(null);

            if (!inputList.isEmpty())
            {
                long ext1 = fluidInput1.take(fluidStorage, transactionContext);
                long ext2 = fluidInput2.take(fluidStorage, transactionContext);
                Item item = itemInput.getFirstMatching(inventory.getItemInput()).orElseThrow(() -> new IllegalStateException("Storage contents must conform to recipe"));
                long ext3 = inventory.getItemInput().extract(ItemVariant.of(item), itemInput.amount(), inner);

                if (ext1 == fluidInput1.amount() && ext2 == fluidInput2.amount() && ext3 == itemInput.amount())
                {
                    inner.commit();
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
                long ins = output.insert((FluidVariant) fluidOutput.resource(), fluidOutput.amount(), transaction);
                if (ins == fluidOutput.amount())
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
    public ItemStack craft(MixerStorage inventory)
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            if (!takeInputs(inventory, transaction))
            {
                transaction.abort();
                return getOutput();
            }

            if (!ejectOutput(inventory, transaction))
            {
                transaction.abort();
                return getOutput();
            }

            transaction.commit();
        }
        return getOutput();
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getOutput()
    {
        return ItemStack.EMPTY;
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

    @Override
    public boolean isIgnoredInRecipeBook()
    {
        return true;
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
            FluidIngredient fluidInput1 = FluidIngredient.fromJson(fluidElement1);

            JsonObject fluidElement2 = JsonHelper.getObject(json, "fluid2");
            FluidIngredient fluidInput2 = FluidIngredient.fromJson(fluidElement2);

            JsonObject fluidElement3 = JsonHelper.getObject(json, "output");
            FluidIngredient fluidOutput = FluidIngredient.fromJson(fluidElement3);

            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput, fluidInput1, fluidInput2, fluidOutput, time);
        }

        @Override
        public MixingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Item> ingredient = RecipeInput.fromBuffer(buf);
            FluidIngredient fluidInput1 = FluidIngredient.fromPacket(buf);
            FluidIngredient fluidInput2 = FluidIngredient.fromPacket(buf);
            FluidIngredient fluidOutput = FluidIngredient.fromPacket(buf);
            int time = buf.readVarInt();

            return this.factory.create(id, ingredient, fluidInput1, fluidInput2, fluidOutput, time);
        }

        @Override
        public void write(PacketByteBuf buf, MixingRecipe recipe)
        {
            recipe.itemInput.write(buf);
            recipe.fluidInput1.write(buf);
            recipe.fluidInput2.write(buf);
            recipe.fluidOutput.write(buf);
            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends MixingRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> var3, FluidIngredient f1, FluidIngredient f2, FluidIngredient out, int time);
        }
    }

}
