package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.RecipeInput;
import com.neep.meatlib.recipe.RecipeOutput;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.grinder.GrinderStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class GrindingRecipe implements Recipe<GrinderStorage>
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

    @Override
    public boolean matches(GrinderStorage inventory, World world)
    {
        return itemInput.test(inventory.getInputStorage());
    }

    @Override
    public ItemStack craft(GrinderStorage inventory)
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
    public RecipeSerializer<?> getSerializer()
    {
        return NMrecipeTypes.GRINDING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.GRINDING;
    }

    public int getTime()
    {
        return processTime;
    }

    public boolean takeInputs(GrinderStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            Optional<Item> item = itemInput.getFirstMatching(storage.getInputStorage());
            if (item.isEmpty())
            {
                throw new IllegalStateException("Storage contents must conform to recipe");
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

    public boolean ejectOutput(GrinderStorage storage, TransactionContext transaction)
    {
        try (Transaction inner = transaction.openNested())
        {
            itemOutput.update();
//            long inserted = storage.getOutputStorage().insert(ItemVariant.of(itemOutput.resource()), itemOutput.amount(), transaction);

            boolean bl1 = itemOutput.insertInto(storage.getOutputStorage(), ItemVariant::of, inner);
            boolean bl2 = extraOutput == null || extraOutput.insertInto(storage.getExtraStorage(), ItemVariant::of, inner);
            boolean bl3 = storage.getXpStorage().insert(experience, transaction) == experience;

            if (bl1 && bl2 && bl3)
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    public static class Serializer implements RecipeSerializer<GrindingRecipe>
    {
        RecipeFactory<GrindingRecipe> factory;
        int processTIme;

        public Serializer(RecipeFactory<GrindingRecipe> recipeFactory, int processTime)
        {
            this.factory = recipeFactory;
            this.processTIme = processTime;
        }

        @Override
        public GrindingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject inputElement = JsonHelper.getObject(json, "input");
            RecipeInput<Item> itemInput = RecipeInput.fromJson(Registry.ITEM, inputElement);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Item> itemOutput = RecipeOutput.fromJson(Registry.ITEM, outputElement);

            // Extra output is optional in recipe json
            RecipeOutput<Item> extraOutput = null;
            if (json.has("extra"))
            {
                JsonObject extraElement = JsonHelper.getObject(json, "extra");
                extraOutput = RecipeOutput.fromJson(Registry.ITEM, extraElement);
            }

            float experience = JsonHelper.getFloat(json, "experience", 0);

            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput, itemOutput, extraOutput, experience, time);
        }

        @Override
        public GrindingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Item> itemInput = RecipeInput.fromBuffer(Registry.ITEM, buf);
            RecipeOutput<Item> itemOutput = RecipeOutput.fromBuffer(Registry.ITEM, buf);

            RecipeOutput<Item> extraOutput = null;
            if (buf.readBoolean())
            {
                extraOutput = RecipeOutput.fromBuffer(Registry.ITEM, buf);
            }

            float experience = buf.readFloat();
            int time = buf.readVarInt();

            return this.factory.create(id, itemInput, itemOutput, extraOutput, experience, time);
        }

        @Override
        public void write(PacketByteBuf buf, GrindingRecipe recipe)
        {
            recipe.itemInput.write(Registry.ITEM, buf);
            recipe.itemOutput.write(Registry.ITEM, buf);

            // Include extra only if present
            if (recipe.extraOutput != null)
            {
                buf.writeBoolean(true);
                recipe.extraOutput.write(Registry.ITEM, buf);
            }
            else buf.writeBoolean(false);

            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends GrindingRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> in, RecipeOutput<Item> out, @Nullable RecipeOutput<Item> eOut, float xp, int time);
        }
    }
}
