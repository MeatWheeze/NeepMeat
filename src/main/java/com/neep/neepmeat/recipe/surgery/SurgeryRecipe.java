package com.neep.neepmeat.recipe.surgery;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.surgical_controller.SurgeryTableContext;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class SurgeryRecipe implements MeatRecipe<SurgeryTableContext>
{
    private DefaultedList<BlockApiLookup<Storage<?>, Direction>> lookups;
    private DefaultedList<RecipeInput<?>> ingredients;
    private final Identifier id;
    public SurgeryRecipe(Identifier id)
    {
        this.id = id;
    }

    @Override
    public boolean matches(SurgeryTableContext context)
    {
        // 1. Does the block expose a valid Surgery Table Structure API?
        // 2. Does the reported storage lookup match the lookup in the recipe?
        // 3. Does the storage match the ingredient's conditions?

        for (int i = 0; i < 9; ++i)
        {
            RecipeInput<?> input = ingredients.get(i);
            TableComponent<? extends TransferVariant<?>> structure = context.getStructure(i);
            if (structure == null || Objects.equals(structure.getSidedLookup(), lookups.get(i))) return false;
            try (Transaction transaction = Transaction.openOuter())
            {
                if (input.test(structure.getStorage(), transaction))
                {
                    transaction.abort();
                    return true;
                }
                transaction.abort();
            }
        }
        return false;
    }

    @Override
    public boolean takeInputs(SurgeryTableContext context, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public boolean ejectOutputs(SurgeryTableContext context, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return null;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return NMrecipeTypes.SURGERY_SERIALIZER;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    public static class Serializer implements MeatRecipeSerialiser<SurgeryRecipe>
    {
        final RecipeFactory<SurgeryRecipe> factory;

        public Serializer(RecipeFactory<SurgeryRecipe> factory)
        {
            this.factory = factory;
        }

        int processTIme;

        public Serializer(RecipeFactory<SurgeryRecipe> recipeFactory, int processTime)
        {
            this.factory = recipeFactory;
            this.processTIme = processTime;
        }

        @Override
        public SurgeryRecipe read(Identifier id, JsonObject json)
        {
            return this.factory.create(id);
        }

        @Override
        public SurgeryRecipe read(Identifier id, PacketByteBuf buf)
        {
            return this.factory.create(id);
        }

        @Override
        public void write(PacketByteBuf buf, SurgeryRecipe recipe)
        {
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends SurgeryRecipe>
        {
            T create(Identifier var1);
        }
    }
}
