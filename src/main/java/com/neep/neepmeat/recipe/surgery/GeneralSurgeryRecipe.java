package com.neep.neepmeat.recipe.surgery;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.surgical_controller.SurgeryTableContext;
import com.neep.neepmeat.plc.component.TableComponent;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Deprecated
@SuppressWarnings("UnstableApiUsage")
public class GeneralSurgeryRecipe extends SurgeryRecipe
{
    public static final Map<Identifier, Function<Object, TransferVariant<?>>> TRANSFER_MAP = new HashMap<>();

    private static final Function<?, TransferVariant<?>> ITEM = TRANSFER_MAP.put(RecipeInputs.ITEM_ID, v -> ItemVariant.of((Item) v));
    private static final Function<?, TransferVariant<?>> FLUID = TRANSFER_MAP.put(RecipeInputs.FLUID_ID, v -> FluidVariant.of((Fluid) v));

    private final int width;
    private final int height;
    private final DefaultedList<RecipeInput<?>> inputs;
    private final RecipeOutputImpl<Item> output;
    private final Identifier id;
    public GeneralSurgeryRecipe(Identifier id, int w, int h, DefaultedList<RecipeInput<?>> inputs, RecipeOutputImpl<Item> output)
    {
        this.id = id;
        this.width = w;
        this.height = h;
        this.inputs = inputs;
        this.output = output;
    }

    @Override
    public boolean matches(SurgeryTableContext context)
    {
        // 1. Does the block expose a valid Surgery Table Structure API?
        // 2. Does the reported storage lookup match the lookup in the recipe?
        // 3. Does the storage match the ingredient's conditions?

        for (int u = 0; u < width; ++u) for (int v = 0; v < height; ++v)
        {
            int index = v * height + u;
            RecipeInput<?> input = inputs.get(index);
            TableComponent<? extends TransferVariant<?>> structure = context.getStructure(index);

            // Pass missing or invalid structures if the corresponding input does not care
            if (input.isEmpty()) continue;

            // Fail if types do not match or the structure is invalid
            if (structure == null || !Objects.equals(structure.getType(), input.getType()))
                return false;

            try (Transaction transaction = Transaction.openOuter())
            {
                if (!input.testStorage(structure.getStorage()))
                {
                    transaction.abort();
                    return false;
                }
                transaction.abort();
            }
        }
        return true;
    }

    public boolean takeInput(SurgeryTableContext context, int i, TransactionContext transaction)
    {
        RecipeInput<?> input = inputs.get(i);
        TableComponent<TransferVariant<?>> component = context.getStructure(i);

        // Abort if the structure is invalid (block has probably been broken)
        if (component == null) return false;

        Storage<TransferVariant<?>> storage = component.getStorage();
        Optional<?> matching = input.getFirstMatching(storage, transaction);
        if (matching.isPresent())
        {
            Transaction inner = transaction.openNested();
            TransferVariant<?> variant = TRANSFER_MAP.get(input.getType()).apply(matching.get());
            if (storage.extract(variant, input.amount(), inner) == input.amount())
            {
                inner.commit();
                return true;
            }
            inner.abort();
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
        output.update();
        output.insertInto(context.getStorage(), ItemVariant::of, transaction);
        return false;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.SURGERY;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.SURGERY_SERIALIZER;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    public DefaultedList<RecipeInput<?>> getInputs()
    {
        return inputs;
    }

    public int getGridSize()
    {
        return width * height;
    }

    public int getHeight()
    {
        return 3;
    }

    public int getWidth()
    {
        return 3;
    }

    public RecipeOutputImpl<Item> getIOutput()
    {
        return output;
    }

    public boolean isInputEmpty(int recipeProgress)
    {
        return inputs.get(recipeProgress).isEmpty();
    }

    public static class Serializer implements MeatRecipeSerialiser<GeneralSurgeryRecipe>
    {
        public Serializer()
        {
        }

//        public Serializer(int processTime)
//        {
//            this.factory = recipeFactory;
//            this.processTIme = processTime;
//        }

        protected static Map<String, RecipeInput<?>> readSymbols(JsonObject json)
        {
            Map<String, RecipeInput<?>> map = Maps.newHashMap();

            for (Map.Entry<String, JsonElement> entry : json.entrySet())
            {
                if ((entry.getKey()).length() != 1)
                {
                    throw new JsonSyntaxException("Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
                }
                if (" ".equals(entry.getKey()))
                {
                    throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
                }
                map.put(entry.getKey(), RecipeInput.fromJson((JsonObject) entry.getValue()));
            }
            map.put(" ", RecipeInputs.EMPTY);
            return map;
        }

        protected static String[] getPattern(JsonArray json)
        {
            String[] strings = new String[3];
            if (json.size() != 3)
            {
                throw new JsonSyntaxException("Invalid pattern: incorrect number of columns.");
            }
            else
            {
                for (int i = 0; i < json.size(); ++i)
                {
                    String string = JsonHelper.asString(json.get(i), "pattern[" + i + "]");
                    if (string.length() != 3)
                    {
                        throw new JsonSyntaxException("Invalid pattern: incorrect number of rows.");
                    }

                    if (i > 0 && strings[0].length() != string.length())
                    {
                        throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                    }

                    strings[i] = string;
                }
                return strings;
            }
        }

        // Creates a width×height list of inputs
        protected static DefaultedList<RecipeInput<?>> createPatternMatrix(String[] pattern, Map<String, RecipeInput<?>> symbols, int width, int height)
        {
            DefaultedList<RecipeInput<?>> defaultedList = DefaultedList.ofSize(width * height, RecipeInputs.EMPTY);

            for(int i = 0; i < pattern.length; ++i)
            {
                for(int j = 0; j < pattern[i].length(); ++j)
                {
                    String symbol = pattern[i].substring(j, j + 1);
                    RecipeInput<?> input = symbols.get(symbol);
                    if (input == null) throw new JsonSyntaxException("Pattern references symbol '" + symbol + "' but it's not defined in the key");

                    defaultedList.set(j + width * i, input);
                }
            }

            return defaultedList;
        }

        @Override
        public GeneralSurgeryRecipe read(Identifier id, JsonObject json)
        {
            Map<String, RecipeInput<?>> map = Serializer.readSymbols(JsonHelper.getObject(json, "key"));
            String[] strings = getPattern(JsonHelper.getArray(json, "pattern"));
            int w = strings[0].length();
            int h = strings.length;
            DefaultedList<RecipeInput<?>> inputs = createPatternMatrix(strings, map, w, h);
            RecipeOutputImpl<Item> output = RecipeOutputImpl.fromJsonRegistry(Registries.ITEM, JsonHelper.getObject(json, "result"));
            return new GeneralSurgeryRecipe(id, w, h, inputs, output);
        }

        @Override
        public GeneralSurgeryRecipe read(Identifier id, PacketByteBuf buf)
        {
            int width = buf.readVarInt();
            int height = buf.readVarInt();

            DefaultedList<RecipeInput<?>> inputs = DefaultedList.ofSize(width * height, RecipeInputs.EMPTY);

            inputs.replaceAll(ignored -> RecipeInput.fromBuffer(buf));

            RecipeOutputImpl<Item> output = RecipeOutputImpl.fromBuffer(Registries.ITEM, buf);
            return new GeneralSurgeryRecipe(id, width, height, inputs, output);
        }

        @Override
        public void write(PacketByteBuf buf, GeneralSurgeryRecipe recipe)
        {
            buf.writeVarInt(recipe.width);
            buf.writeVarInt(recipe.height);

            for (RecipeInput<?> input : recipe.inputs)
            {
                input.write(buf);
            }

            recipe.output.write(Registries.ITEM, buf);
        }
    }
}
