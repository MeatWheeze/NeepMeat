package com.neep.neepmeat.plc.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.plc.component.MutateInPlace;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class EntityToItemRecipe extends EntityMutateRecipe
{
    private final RecipeOutput<Item> output;

    public EntityToItemRecipe(Identifier id, EntityType<?> base, List<ManufactureStep<?>> steps, RecipeOutput<Item> output)
    {
        super(id, base, steps);
        this.output = output;
    }

    @Override
    public boolean matches(MutateInPlace<Entity> context)
    {
        Entity entity = context.get();

        if (!entity.getType().equals(base))
            return false;

        var workpiece = NMComponents.WORKPIECE.getNullable(entity);
        if (workpiece != null)
        {
            var workSteps = workpiece.getSteps();

            if (workSteps.size() < steps.size())
                return false;

            int difference = workSteps.size() - steps.size();

            int i = steps.size() - 1;
            while (i >= 0)
            {
                var workStep = workSteps.get(i + difference);
                var recipeStep = steps.get(i);

                if (!ManufactureStep.equals(workStep, recipeStep))
                {
                    return false;
                }

                i--;
            }
            return true;
        }
        return false;
    }

    @Override
    public EntityType<?> getBase()
    {
        return base;
    }

    public List<ManufactureStep<?>> getSteps()
    {
        return steps;
    }

    @Override
    public boolean takeInputs(MutateInPlace<Entity> context, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public boolean ejectOutputs(MutateInPlace<Entity> context, TransactionContext transaction)
    {
        Entity entity = context.get();
        Vec3d pos = entity.getPos();

        World world = entity.getWorld();
        int amount = (int) output.randomAmount(0);
        ItemStack stack = new ItemStack(output.resource(), amount);
        world.spawnEntity(new ItemEntity(world, pos.x, pos.y + 0.1, pos.z, stack, 0, 0.1, 0));
        entity.discard();

        return false;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return PLCRecipes.ENTITY_TO_ITEM;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return PLCRecipes.ENTITY_TO_ITEM_SERIALISER;
    }


    public static class Serialiser implements MeatRecipeSerialiser<EntityToItemRecipe>
    {
        @Override
        public EntityToItemRecipe read(Identifier id, JsonObject json)
        {
            JsonObject baseElement = JsonHelper.getObject(json, "base");
            String idString = JsonHelper.getString(baseElement, "id");
            EntityType<?> base = Registries.ENTITY_TYPE.get(Identifier.tryParse(idString));

            List<ManufactureStep<?>> steps = ItemManufactureRecipe.Serialiser.readSteps(json);

            RecipeOutput<Item> output = RecipeOutputImpl.fromJsonRegistry(Registries.ITEM, json.getAsJsonObject("result"));

            return new EntityToItemRecipe(id, base, steps, output);
        }

        @Override
        public EntityToItemRecipe read(Identifier id, PacketByteBuf buf)
        {
            EntityType<?> base = buf.readRegistryValue(Registries.ENTITY_TYPE);

            List<ManufactureStep<?>> steps = ItemManufactureRecipe.Serialiser.readSteps(buf);

            RecipeOutput<Item> output = RecipeOutputImpl.fromBuffer(Registries.ITEM, buf);

            return new EntityToItemRecipe(id, base, steps, output);
        }

        @Override
        public void write(PacketByteBuf buf, EntityToItemRecipe recipe)
        {
            buf.writeRegistryValue(Registries.ENTITY_TYPE, recipe.base);

            ItemManufactureRecipe.Serialiser.writeSteps(recipe.getSteps(), buf);

            recipe.output.write(Registries.ITEM, buf);
        }
    }
}
