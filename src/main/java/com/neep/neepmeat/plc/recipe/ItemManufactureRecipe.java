package com.neep.neepmeat.plc.recipe;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.plc.component.MutateInPlace;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class ItemManufactureRecipe implements ManufactureRecipe<MutateInPlace<ItemStack>>
{
    protected final Identifier id;
    protected final Item base;
    protected final RecipeOutput<Item> output;
    protected List<ManufactureStep<?>> steps;

    public ItemManufactureRecipe(Identifier id, Item base, RecipeOutput<Item> output, List<ManufactureStep<?>> steps)
    {
        this.id = id;
        this.base = base;
        this.output = output;
        this.steps = steps;
    }

    @Override
    public Object getBase()
    {
        return base;
    }

    public List<ManufactureStep<?>> getSteps()
    {
        return steps;
    }

    @Override
    public boolean matches(MutateInPlace<ItemStack> context)
    {
        ItemStack stack = context.get();

        if (!stack.isOf(base))
            return false;

        var workpiece = NMComponents.WORKPIECE.maybeGet(stack).orElse(null);
        if (workpiece != null)
        {
            var workSteps = workpiece.getSteps();

            if (workSteps.size() != steps.size())
                return false;

            for (int i = 0; i < workSteps.size(); ++i)
            {
                var step = workSteps.get(i);

                if (!ManufactureStep.equals(step, steps.get(i)))
                {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean takeInputs(MutateInPlace<ItemStack> context, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public boolean ejectOutputs(MutateInPlace<ItemStack> context, TransactionContext transaction)
    {
        ItemStack outputStack = new ItemStack(output.resource(), Math.toIntExact(output.amount()));
        context.set(outputStack);
        return true;
    }

    public RecipeOutput<Item> getOutput()
    {
        return output;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return PLCRecipes.MANUFACTURE;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return PLCRecipes.MANUFACTURE_SERIALISER;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    public static class Serialiser implements MeatRecipeSerialiser<ItemManufactureRecipe>
    {
        public static List<ManufactureStep<?>> readSteps(JsonObject root)
        {
            JsonArray stepElement = JsonHelper.getArray(root, "steps");
            List<ManufactureStep<?>> steps = Lists.newArrayList();
            for (var element : stepElement)
            {
                JsonObject object = JsonHelper.asObject(element, element.toString());

                String stepIdString = JsonHelper.getString(object, "id");
                Identifier stepId = Identifier.tryParse(stepIdString);
                if (stepId == null)
                    throw new JsonParseException("Invalid step ID: " + stepIdString);

                var provider = ManufactureStep.REGISTRY.get(stepId);

                if (provider == null)
                    throw new JsonParseException("Unknown step ID: " + stepIdString);

                ManufactureStep<?> step = provider.create(object);

                steps.add(step);
            }
            return steps;
        }

        public static void writeSteps(List<ManufactureStep<?>> steps, PacketByteBuf buf)
        {
            buf.writeInt(steps.size());
            for (var step : steps)
            {
                buf.writeNbt(step.toNbt());
                buf.writeRegistryValue(ManufactureStep.REGISTRY, ManufactureStep.REGISTRY.get(step.getId()));
            }
        }

        public static List<ManufactureStep<?>> readSteps(PacketByteBuf buf)
        {
            int size = buf.readInt();
            List<ManufactureStep<?>> steps = Lists.newArrayList();
            for (int i = 0; i < size; ++i)
            {
                NbtCompound data = buf.readNbt();

                var step = buf.readRegistryValue(ManufactureStep.REGISTRY).create(data);
                steps.add(step);
            }
            return steps;
        }

        @Override
        public ItemManufactureRecipe read(Identifier id, JsonObject json)
        {
            JsonObject baseElement = JsonHelper.getObject(json, "base");
            String idString = JsonHelper.getString(baseElement, "id");
            Identifier baseId = Identifier.tryParse(idString);
            Item base = Registry.ITEM.get(baseId);

            List<ManufactureStep<?>> steps = readSteps(json);

//            JsonObject outputObject = JsonHelper.getObject(json, "output");
//            Identifier outputId = Identifier.tryParse(JsonHelper.getString(outputObject, "resource"));
//            Item output = Registry.ITEM.get(outputId);

            RecipeOutputImpl<Item> output = RecipeOutputImpl.fromJsonRegistry(Registry.ITEM, JsonHelper.getObject(json, "result"));

            return new ItemManufactureRecipe(id, base, output, steps);
        }

        @Override
        public ItemManufactureRecipe read(Identifier id, PacketByteBuf buf)
        {
            Item base = buf.readRegistryValue(Registry.ITEM);

            List<ManufactureStep<?>> steps = readSteps(buf);

            RecipeOutput<Item> output = RecipeOutputImpl.fromBuffer(Registry.ITEM, buf);

            return new ItemManufactureRecipe(id, base, output, steps);
        }

        @Override
        public void write(PacketByteBuf buf, ItemManufactureRecipe recipe)
        {
            buf.writeRegistryValue(Registry.ITEM, recipe.base);

            writeSteps(recipe.getSteps(), buf);

            recipe.output.write(Registry.ITEM, buf);
        }
    }
}
