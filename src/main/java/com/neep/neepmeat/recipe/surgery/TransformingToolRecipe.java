package com.neep.neepmeat.recipe.surgery;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.item.TransformingTools;
import com.neep.neepmeat.machine.surgical_controller.SurgeryTableContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.neep.neepmeat.recipe.surgery.GeneralSurgeryRecipe.Serializer.getPattern;
import static com.neep.neepmeat.recipe.surgery.GeneralSurgeryRecipe.Serializer.createPatternMatrix;

@SuppressWarnings("UnstableApiUsage")
public class TransformingToolRecipe extends GeneralSurgeryRecipe
{
    public TransformingToolRecipe(Identifier id, int w, int h, DefaultedList<RecipeInput<?>> inputs)
    {
        super(id, w, h, inputs, new RecipeOutputImpl<>(Items.STONE, 0, 0));
    }

    @Override
    public boolean matches(SurgeryTableContext context)
    {
        boolean superMatches = super.matches(context);

        // Check that the combining item storages are not empty.
        if (context.getStructure(3) == null || context.getStructure(5) == null) return false;
        List<TableComponent<? extends TransferVariant<?>>> structures = List.of(context.getStructure(3),  context.getStructure(5));
        try (Transaction transaction = Transaction.openOuter())
        {
            for (TableComponent<? extends TransferVariant<?>> structure : structures)
            {
                if (!Objects.equals(structure.getType(), RecipeInputs.ITEM_ID)) return false;

                // Iterate through all storage views. If at least one is not empty, pass.
                boolean containsItem = false;
                for (StorageView<? extends TransferVariant<?>> view : structure.getStorage().iterable(transaction))
                {
                    if (view.getAmount() != 0) containsItem = true;
                }
                if (!containsItem)
                    return false;
            }
        }
        return superMatches;
    }

    @Override
    public boolean isInputEmpty(int i)
    {
        boolean b = super.isInputEmpty(i) || isCombineInput(i);
        return b;
    }

    protected boolean isCombineInput(int i)
    {
        return i == 5 || i == 3;
    }

    @Override
    public boolean takeInput(SurgeryTableContext context, int i, TransactionContext transaction)
    {
        if (isCombineInput(i)) return true;
        return super.takeInput(context, i, transaction);
    }

    @Override
    public boolean ejectOutputs(SurgeryTableContext context, TransactionContext transaction)
    {
        TableComponent<TransferVariant<?>> structure1 = context.getStructure(3);
        TableComponent<TransferVariant<?>> structure2 = context.getStructure(5);

        // Check that blocks have not been removed or replaced with a different storage type
        if (structure1 == null || structure2 == null
                || !structure1.getType().equals(RecipeInputs.ITEM_ID) || !structure2.getType().equals(RecipeInputs.ITEM_ID)) return false;

        try (Transaction inner = transaction.openNested())
        {
            ResourceAmount<TransferVariant<?>> amount1 = StorageUtil.findExtractableContent(structure1.getStorage(), inner);
            ResourceAmount<TransferVariant<?>> amount2 = StorageUtil.findExtractableContent(structure2.getStorage(), inner);

            if (amount1 == null || amount2 == null)
            {
                inner.abort();
                return false;
            }

            long ext1 = structure1.getStorage().extract(amount1.resource(), 1, inner);
            long ext2 = structure2.getStorage().extract(amount2.resource(), 1, inner);

            if (ext1 == 1 && ext2 == 1)
            {
                ItemVariant combined = TransformingTools.combine((ItemVariant) amount1.resource(), (ItemVariant) amount2.resource());
                if (combined != null)
                {
                    context.getStorage().insert(combined, 1, inner);
                    inner.commit();
                    return true;
                }
            }

            inner.abort();
        }

        return false;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.TRANSFORMING_TOOL;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return NMrecipeTypes.TRANSFORMING_TOOL_SERIALISER;
    }

    public static class Serialiser implements MeatRecipeSerialiser<TransformingToolRecipe>
    {
        @Override
        public TransformingToolRecipe read(Identifier id, JsonObject json)
        {
            Map<String, RecipeInput<?>> map = Serializer.readSymbols(JsonHelper.getObject(json, "key"));
            String[] strings = getPattern(JsonHelper.getArray(json, "pattern"));
            int w = strings[0].length();
            int h = strings.length;
            DefaultedList<RecipeInput<?>> inputs = createPatternMatrix(strings, map, w, h);
            return new TransformingToolRecipe(id, w, h, inputs);
        }

        @Override
        public TransformingToolRecipe read(Identifier id, PacketByteBuf buf)
        {
            int width = buf.readVarInt();
            int height = buf.readVarInt();

            DefaultedList<RecipeInput<?>> inputs = DefaultedList.ofSize(width * height, RecipeInputs.EMPTY);

            inputs.replaceAll(ignored -> RecipeInput.fromBuffer(buf));

            return new TransformingToolRecipe(id, width, height, inputs);
        }

        @Override
        public void write(PacketByteBuf buf, TransformingToolRecipe recipe)
        {
            buf.writeVarInt(recipe.getWidth());
            buf.writeVarInt(recipe.getHeight());

            for (RecipeInput<?> input : recipe.getInputs())
            {
                input.write(buf);
            }
        }
    }
}
