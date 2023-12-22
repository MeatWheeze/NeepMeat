package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.RecipeInput;
import com.neep.meatlib.recipe.RecipeOutput;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.pedestal.PedestalBlockEntity;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class EnlighteningRecipe extends ImplementedRecipe<PedestalBlockEntity.RecipeBehaviour>
{
    protected Identifier id;
    protected RecipeInput<Item> itemInput;
    protected RecipeOutput<Item> itemOutput;
    protected int data;

    public EnlighteningRecipe(Identifier id, RecipeInput<Item> fluidInput, RecipeOutput<Item> itemOutput, int data)
    {
        this.itemInput = fluidInput;
        this.itemOutput = itemOutput;
        this.data = data;
        this.id = id;
    }

    @Override
    public boolean matches(PedestalBlockEntity.RecipeBehaviour inventory, World world)
    {
        return itemInput.test(inventory.getStorage())
                && inventory.getIntegrator() != null
                && inventory.getIntegrator().getData() >= data;
    }

    public RecipeInput<Item> getItemInput()
    {
        return itemInput;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return NMrecipeTypes.ENLIGHTENING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.ENLIGHTENING;
    }

    public ItemVariant craft(PedestalBlockEntity.RecipeBehaviour storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            WritableStackStorage stackStorage = storage.getStorage();
//
//            // Ensure that storage contents still match the recipe
            long amount = stackStorage.getAmount();
            Item input = itemInput.getFirstMatching(stackStorage).orElse(null);
            IntegratorBlockEntity integrator = storage.getIntegrator();
            float storedData = integrator.getData();
            if (input == null || storedData < data)
            {
                return null;
            }

            long ext = stackStorage.extract(stackStorage.getResource(), amount * itemInput.amount(), inner);
            float dataExt = integrator.extractEnlightenment(amount * data, transaction);


            if (ext != amount * itemInput.amount() || dataExt != amount * data)
            {
                inner.abort();
                return null;
            }

            itemOutput.update();
            long ins = stackStorage.insert(ItemVariant.of(itemOutput.resource()), amount * itemOutput.amount(), inner);

            if (ins == amount * itemOutput.amount())
            {
                inner.commit();
                return null;
            }
            inner.abort();
        }
        return null;
    }

    public RecipeOutput<Item> getItemOutput()
    {
        return itemOutput;
    }

    public float getData()
    {
        return data;
    }

    public static class Serializer implements RecipeSerializer<EnlighteningRecipe>
    {
        RecipeFactory<EnlighteningRecipe> factory;

        public Serializer(RecipeFactory<EnlighteningRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public EnlighteningRecipe read(Identifier id, JsonObject json)
        {
            JsonObject itemInputElement = JsonHelper.getObject(json, "item_input");
            RecipeInput<Item> itemInput = RecipeInput.fromJson(Registry.ITEM, itemInputElement);

            JsonObject itemOutputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Item> itemOutput = RecipeOutput.fromJson(Registry.ITEM, itemOutputElement);

            int data = JsonHelper.getInt(json, "data");

            return this.factory.create(id, itemInput, itemOutput, data);
        }

        @Override
        public EnlighteningRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Item> itemInput = RecipeInput.fromBuffer(Registry.ITEM, buf);
            RecipeOutput<Item> itemOutput = RecipeOutput.fromBuffer(Registry.ITEM, buf);
            int data = buf.readVarInt();

            return this.factory.create(id, itemInput, itemOutput, data);
        }

        @Override
        public void write(PacketByteBuf buf, EnlighteningRecipe recipe)
        {
            recipe.itemInput.write(Registry.ITEM, buf);
            recipe.itemOutput.write(Registry.ITEM, buf);
            buf.writeVarInt(recipe.data);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends EnlighteningRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> in, RecipeOutput<Item> out, int data);
        }
    }
}
