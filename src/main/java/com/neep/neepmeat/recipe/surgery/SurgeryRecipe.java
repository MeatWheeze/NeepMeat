package com.neep.neepmeat.recipe.surgery;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.surgical_controller.SurgeryTableContext;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class SurgeryRecipe implements MeatRecipe<SurgeryTableContext>
{
    private final DefaultedList<BlockApiLookup<Storage<?>, Direction>> lookups;
    private final DefaultedList<RecipeInput<?>> inputs;
    private final RecipeOutput<?> output;
    private final Identifier id;
    public SurgeryRecipe(Identifier id, int w, int h, DefaultedList<RecipeInput<?>> inputs, DefaultedList<BlockApiLookup<Storage<?>, Direction>> lookups, RecipeOutput<?> output)
    {
        this.id = id;
        this.inputs = inputs;
        this.lookups = lookups;
        this.output = output;
    }

    @Override
    public boolean matches(SurgeryTableContext context)
    {
        // 1. Does the block expose a valid Surgery Table Structure API?
        // 2. Does the reported storage lookup match the lookup in the recipe?
        // 3. Does the storage match the ingredient's conditions?

        for (int i = 0; i < 9; ++i)
        {
            RecipeInput<?> input = inputs.get(i);
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

        private static String[] getPattern(JsonArray json)
        {
            String[] strings = new String[json.size()];
            if (strings.length > 3)
            {
                throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
            }
            else if (strings.length == 0)
            {
                throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
            }
            else
            {
                for(int i = 0; i < strings.length; ++i)
                {
                    String string = JsonHelper.asString(json.get(i), "pattern[" + i + "]");
                    if (string.length() > 3)
                    {
                        throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
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

        private static String[] removePadding(String... pattern)
        {
            int i = Integer.MAX_VALUE;
            int j = 0, k = 0, l = 0;

            for(int m = 0; m < pattern.length; ++m)
            {
                String string = pattern[m];
                i = Math.min(i, findFirstSymbol(string));
                int n = findLastSymbol(string);
                j = Math.max(j, n);
                if (n < 0)
                {
                    if (k == m)
                    {
                        ++k;
                    }

                    ++l;
                }
                else
                {
                    l = 0;
                }
            }
            if (pattern.length == l)
            {
                return new String[0];
            }
            else
            {
                String[] strings = new String[pattern.length - l - k];

                for(int o = 0; o < strings.length; ++o)
                {
                    strings[o] = pattern[o + k].substring(i, j + 1);
                }
                return strings;
            }
        }

        private static int findFirstSymbol(String line)
        {
            int i;
            for(i = 0; i < line.length() && line.charAt(i) == ' '; ++i) {}
            return i;
        }

        private static int findLastSymbol(String pattern)
        {
            int i;
            for(i = pattern.length() - 1; i >= 0 && pattern.charAt(i) == ' '; --i) {}
            return i;
        }

        // Creates a width×height list of inputs
        private DefaultedList<RecipeInput<?>> createPatternMatrix(String[] pattern, Map<String, RecipeInput<?>> symbols, int width, int height)
        {
            DefaultedList<RecipeInput<?>> defaultedList = DefaultedList.ofSize(width * height, RecipeInputs.EMPTY);
            Set<String> set = Sets.newHashSet(symbols.keySet());
            set.remove(" ");

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
        public SurgeryRecipe read(Identifier id, JsonObject json)
        {
            Map<String, RecipeInput<?>> map = Serializer.readSymbols(JsonHelper.getObject(json, "key"));
            String[] strings = removePadding(getPattern(JsonHelper.getArray(json, "pattern")));
            int w = strings[0].length();
            int h = strings.length;
            DefaultedList<RecipeInput<?>> inputs = createPatternMatrix(strings, map, w, h);
            RecipeOutput<?> output = RecipeOutput.fromJson(JsonHelper.getObject(json, "result"));
            return new SurgeryRecipe(id, w, h, inputs, null, output);
        }



        @Override
        public SurgeryRecipe read(Identifier id, PacketByteBuf buf)
        {
            return null;
        }

        @Override
        public void write(PacketByteBuf buf, SurgeryRecipe recipe)
        {
        }
    }
}
