package com.neep.neepmeat.machine.mixer;

import com.google.gson.JsonObject;
import com.ibm.icu.impl.TextTrieMap;
import com.neep.meatlib.recipe.FluidIngredient;
import com.neep.meatlib.recipe.ItemIngredient;
import com.neep.neepmeat.init.NMrecipeTypes;
import jdk.jfr.FlightRecorder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;
import org.lwjgl.system.CallbackI;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@SuppressWarnings("UnstableApiUsage")
public class MixingRecipe implements Recipe<MixerStorage>
{
    protected Identifier id;
    protected ItemIngredient itemInput;
    protected FluidIngredient fluidInput1;
    protected FluidIngredient fluidInput2;
    protected FluidIngredient fluidOutput;
    protected int processTime;

    public MixingRecipe(Identifier id, ItemIngredient itemInput, FluidIngredient fluidInput1, FluidIngredient fluidInput2, FluidIngredient fluidOutput, int processTime)
    {
        this.itemInput = itemInput;
        this.fluidInput1 = fluidInput1;
        this.fluidInput2 = fluidInput2;
        this.fluidOutput = fluidOutput;
        this.processTime = processTime;
        this.id = id;
    }

    @Override
    public boolean matches(MixerStorage inventory, World world)
    {
        Transaction transaction = Transaction.openOuter();
        short success = 0;
        Queue<FluidIngredient> queue = new LinkedList<>(List.of(fluidInput1, fluidInput2));

        while (!queue.isEmpty())
        {
            FluidIngredient ingredient = queue.poll();
            for (StorageView<FluidVariant> view : inventory.getFluidInputs(transaction))
            {
                if (ingredient.test(view))
                {
                    ++success;
                }
            }
        }
        transaction.abort();
        return success == 2;
    }

    public boolean takeInputs(MixerStorage inventory, TransactionContext transactionContext)
    {
        try (Transaction inner = transactionContext.openNested())
        {
            List<Storage<FluidVariant>> inputList = inventory.parent.getAdjacentStorages();
            CombinedStorage<FluidVariant, Storage<FluidVariant>> input = new CombinedStorage<>(inputList);
//            CombinedStorage<FluidVariant, Storage<FluidVariant>> internal = new CombinedStorage<>(List.of(inventory.fluidInput1, inventory.fluidInput2));
            if (!inputList.isEmpty())
            {
                long ext1 = input.extract((FluidVariant) fluidInput1.resource(), fluidInput1.amount(), transactionContext);
                long ext2 = input.extract((FluidVariant) fluidInput2.resource(), fluidInput2.amount(), transactionContext);
//                long ins1 = internal.insert((FluidVariant) fluidInput1.resource(), fluidInput1.amount(), transactionContext);
//                long ins2 = internal.insert((FluidVariant) fluidInput2.resource(), fluidInput2.amount(), transactionContext);
                if (ext1 == fluidInput1.amount() && ext2 == fluidInput2.amount())
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
        return NMrecipeTypes.MIXING;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.MIXING_TYPE;
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
            ItemIngredient itemInput = ItemIngredient.fromJson(itemElement);

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
            ItemIngredient ingredient = ItemIngredient.fromBuffer(buf);
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
            T create(Identifier var1, ItemIngredient var3, FluidIngredient f1, FluidIngredient f2, FluidIngredient out, int time);
        }
    }

}
