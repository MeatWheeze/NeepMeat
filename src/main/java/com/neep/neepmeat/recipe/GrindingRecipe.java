package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.MeatlibRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.grinder.IGrinderStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class GrindingRecipe implements MeatlibRecipe<IGrinderStorage>
{
    protected Identifier id;
    protected RecipeInput<Item> itemInput;
    protected RecipeOutput<Item> itemOutput;
    protected RecipeOutput<Item> extraOutput;
    protected float experience;
    protected int processTime;

    public GrindingRecipe(Identifier id, RecipeInput<Item> itemInput, RecipeOutput<Item> itemOutput, RecipeOutput<Item> extraOutput, float experience, int processTime)
    {
        this.itemInput = itemInput;
        this.itemOutput = itemOutput;
        this.extraOutput = extraOutput;
        this.experience = experience;
        this.processTime = processTime;
        this.id = id;
    }

    public boolean destroy()
    {
        return false;
    }

    @Override
    public boolean matches(IGrinderStorage inventory)
    {
        return itemInput.testStorage(inventory.getInputStorage());
    }

    public RecipeInput<Item> getItemInput()
    {
        return itemInput;
    }

    public RecipeOutput<Item> getItemOutput()
    {
        return itemOutput;
    }

    public RecipeOutput<Item> getAuxOutput()
    {
        return extraOutput;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.GRINDING_SERIALIZER;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.GRINDING;
    }

    public int getTime()
    {
        return processTime;
    }

    @Override
    public boolean takeInputs(IGrinderStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            Optional<Item> item = itemInput.getFirstMatching(storage.getInputStorage(), transaction);
            if (item.isEmpty())
            {
                inner.abort();
                return false;
//                throw new IllegalStateException("Storage contents must conform to recipe");
            }

            long extracted = storage.getInputStorage().extract(ItemVariant.of(item.get()), itemInput.amount(), transaction);
            if (extracted == itemInput.amount())
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    @Override
    public boolean ejectOutputs(IGrinderStorage context, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            itemOutput.update();
//            long inserted = storage.getOutputStorage().insert(ItemVariant.of(itemOutput.resource()), itemOutput.amount(), transaction);

            boolean bl1 = itemOutput.insertInto(context.getOutputStorage(), ItemVariant::of, inner);
            boolean bl2 = extraOutput == null || extraOutput.insertInto(context.getExtraStorage(), ItemVariant::of, inner);
            boolean bl3 = context.getXpStorage().insert(experience, transaction) == experience;

            if (bl1 && bl2 && bl3)
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer<T extends GrindingRecipe> implements MeatRecipeSerialiser<T>
    {
        private RecipeFactory<T> factory;
        private DestroyRecipeFactory<T> destroyFactory;
        private int processTIme;

        public Serializer(RecipeFactory<T> recipeFactory, DestroyRecipeFactory<T> destroyFactory, int processTime)
        {
            this.factory = recipeFactory;
            this.destroyFactory = destroyFactory;
            this.processTIme = processTime;
        }

        @Override
        public T read(Identifier id, JsonObject json)
        {
            if (json.has("destroy") && JsonHelper.getBoolean(json, "destroy"))
            {
                return destroyFactory.create(id);
            }

            JsonObject inputElement = JsonHelper.getObject(json, "input");
            RecipeInput<Item> itemInput = RecipeInput.fromJsonRegistry(RecipeInputs.ITEM, inputElement);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutputImpl<Item> itemOutput = RecipeOutputImpl.fromJsonRegistry(Registries.ITEM, outputElement);

            // Extra output is optional in recipe json
            RecipeOutputImpl<Item> extraOutput = null;
            if (json.has("extra"))
            {
                JsonObject extraElement = JsonHelper.getObject(json, "extra");
                extraOutput = RecipeOutputImpl.fromJsonRegistry(Registries.ITEM, extraElement);
            }

            float experience = JsonHelper.getFloat(json, "experience", 0);

            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput, itemOutput, extraOutput, experience, time);
        }

        @Override
        public T read(Identifier id, PacketByteBuf buf)
        {
            // Ignore everything else
            if (buf.readBoolean())
                return destroyFactory.create(id);

            RecipeInput<Item> itemInput = RecipeInput.fromBuffer(buf);
            RecipeOutputImpl<Item> itemOutput = RecipeOutputImpl.fromBuffer(Registries.ITEM, buf);

            RecipeOutputImpl<Item> extraOutput = null;
            if (buf.readBoolean())
            {
                extraOutput = RecipeOutputImpl.fromBuffer(Registries.ITEM, buf);
            }

            float experience = buf.readFloat();
            int time = buf.readVarInt();

            return this.factory.create(id, itemInput, itemOutput, extraOutput, experience, time);
        }

        @Override
        public void write(PacketByteBuf buf, GrindingRecipe recipe)
        {
            buf.writeBoolean(recipe.destroy());
            recipe.itemInput.write(buf);
            recipe.itemOutput.write(Registries.ITEM, buf);

            // Include extra only if present
            if (recipe.extraOutput != null)
            {
                buf.writeBoolean(true);
                recipe.extraOutput.write(Registries.ITEM, buf);
            }
            else buf.writeBoolean(false);

            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends GrindingRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> in, RecipeOutputImpl<Item> out, @Nullable RecipeOutputImpl<Item> eOut, float xp, int time);
        }

        public interface DestroyRecipeFactory<T extends GrindingRecipe>
        {
            T create(Identifier id);
        }
    }

    // A recipe that destroys any input item, for use in the Large Crusher.
    public static class DestroyRecipe extends GrindingRecipe
    {
        public DestroyRecipe(Identifier id)
        {
            super(id, RecipeInputs.empty(), RecipeOutput.empty(), RecipeOutput.empty(), 0, 40);
        }

        @Override
        public boolean destroy()
        {
            return true;
        }

        @Override
        public boolean matches(IGrinderStorage inventory)
        {
            return false;
        }

        @Override
        public boolean takeInputs(IGrinderStorage storage, TransactionContext transaction)
        {
            var it = storage.getInputStorage().nonEmptyIterator();
            if (it.hasNext())
            {
                StorageView<ItemVariant> view = it.next();
                view.extract(view.getResource(), 1, transaction);
                return true;
            }
            return false;
        }

        @Override
        public boolean ejectOutputs(IGrinderStorage context, TransactionContext transaction)
        {
            return true;
        }
    }
}
