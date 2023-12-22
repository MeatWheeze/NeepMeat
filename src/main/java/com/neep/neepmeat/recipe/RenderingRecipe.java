package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.RecipeInput;
import com.neep.meatlib.recipe.RecipeOutput;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.crucible.CrucibleStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@SuppressWarnings("UnstableApiUsage")
public class RenderingRecipe extends ImplementedRecipe<CrucibleStorage>
{
    protected Identifier id;
    protected RecipeInput<Item> itemInput;
    protected RecipeInput<Fluid> fluidInput;
    protected RecipeOutput<Fluid> fluidOutput;
    protected float experience;
//    protected int processTime;

    public RenderingRecipe(Identifier id, RecipeInput<Item> itemInput, RecipeInput<Fluid> fluidInput, RecipeOutput<Fluid> fluidOutput)
    {
        this.itemInput = itemInput;
        this.fluidInput = fluidInput;
        this.fluidOutput = fluidOutput;
        this.experience = experience;
//        this.processTime = processTime;
        this.id = id;
    }

    @Override
    public boolean matches(CrucibleStorage inventory, World world)
    {
//        return itemInput.test(inventory.getInputStorage());
        return false;
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    public RecipeInput<Item> getItemInput()
    {
        return itemInput;
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
        return NMrecipeTypes.RENDERING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.RENDERING;
    }

    public boolean takeInputs(CrucibleStorage storage, TransactionContext transaction)
    {
//        try (Transaction inner = transaction.openNested())
//        {
//            Optional<Item> item = itemInput.getFirstMatching(storage.getInputStorage());
//            if (item.isEmpty())
//            {
//                throw new IllegalStateException("Storage contents must conform to recipe");
//            }
//
//            long extracted = storage.getInputStorage().extract(ItemVariant.of(item.get()), itemInput.amount(), transaction);
//            if (extracted == itemInput.amount())
//            {
//                inner.commit();
//                return true;
//            }
//            inner.abort();
//        }
        return false;
    }

    public boolean ejectOutput(CrucibleStorage storage, TransactionContext transaction)
    {
//        try (Transaction inner = transaction.openNested())
//        {
//            itemOutput.update();
////            long inserted = storage.getOutputStorage().insert(ItemVariant.of(itemOutput.resource()), itemOutput.amount(), transaction);
//
//            boolean bl1 = itemOutput.insertInto(storage.getOutputStorage(), ItemVariant::of, inner);
//            boolean bl2 = extraOutput == null || extraOutput.insertInto(storage.getExtraStorage(), ItemVariant::of, inner);
//            boolean bl3 = storage.getXpStorage().insert(experience, transaction) == experience;
//
//            if (bl1 && bl2 && bl3)
//            {
//                inner.commit();
//                return true;
//            }
//            inner.abort();
//        }
        return false;
    }

    public static class Serializer implements RecipeSerializer<RenderingRecipe>
    {
        RecipeFactory<RenderingRecipe> factory;

        public Serializer(RecipeFactory<RenderingRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public RenderingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject inputElement = JsonHelper.getObject(json, "item_input");
            RecipeInput<Item> itemInput = RecipeInput.fromJson(Registry.ITEM, inputElement);

            JsonObject fluidInputElement = JsonHelper.getObject(json, "fluid_input");
            RecipeInput<Fluid> fluidInput = RecipeInput.fromJson(Registry.FLUID, fluidInputElement);

            JsonObject outputElement = JsonHelper.getObject(json, "output");
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromJson(Registry.FLUID, outputElement);

            return this.factory.create(id, itemInput, fluidInput, fluidOutput);
        }

        @Override
        public RenderingRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<Item> itemInput = RecipeInput.fromBuffer(Registry.ITEM, buf);
            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(Registry.FLUID, buf);
            RecipeOutput<Fluid> fluidOutput = RecipeOutput.fromBuffer(Registry.FLUID, buf);

            return this.factory.create(id, itemInput, fluidInput, fluidOutput);
        }

        @Override
        public void write(PacketByteBuf buf, RenderingRecipe recipe)
        {
            recipe.itemInput.write(Registry.ITEM, buf);
            recipe.fluidInput.write(Registry.FLUID, buf);
            recipe.fluidOutput.write(Registry.FLUID, buf);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends RenderingRecipe>
        {
            T create(Identifier var1, RecipeInput<Item> in, RecipeInput<Fluid> in2, RecipeOutput<Fluid> out);
        }
    }
}
