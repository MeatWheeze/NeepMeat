package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatlibRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeOutput;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMrecipeTypes;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class VivisectionRecipe implements MeatlibRecipe<VivisectionRecipe.VivisectionContext>
{
    private final Identifier id;
    private final EntityType<?> entityType;
    private final int maxHealth;
    private final RecipeOutput<Item> output;

    public VivisectionRecipe(Identifier id, EntityType<?> entityType, int maxHealth, RecipeOutput<Item> output)
    {
        this.id = id;
        this.entityType = entityType;
        this.maxHealth = maxHealth;
        this.output = output;
    }

    @Override
    public boolean matches(VivisectionContext context)
    {
        return context.entity.getType().equals(entityType) && context.entity.getHealth() <= maxHealth;
    }

    @Override
    public boolean takeInputs(VivisectionContext context, TransactionContext transaction)
    {
        return true;
    }

    @Override
    public boolean ejectOutputs(VivisectionContext context, TransactionContext transaction)
    {
        output.update();
        Item resource = output.resource();
        int amount = (int) output.amount();
        ItemEntity item = new ItemEntity(context.world, context.pos.x, context.pos.y, context.pos.z, new ItemStack(resource, amount));
        context.world.spawnEntity(item);
        context.entity.kill();
        return true;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.VIVISECTION_SERIALISER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.VIVISECTION;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    public EntityType<?> getEntityType()
    {
        return entityType;
    }

    public RecipeOutput<Item> getReicpeOutput()
    {
        return output;
    }

    public static class Serializer implements MeatRecipeSerialiser<VivisectionRecipe>
    {
        @Override
        public VivisectionRecipe read(Identifier id, JsonObject json)
        {
            Identifier entityId = Identifier.tryParse(JsonHelper.getString(json, "entity"));
            var type = Registry.ENTITY_TYPE.getOrEmpty(entityId).orElse(null);
            if (type == null)
                throw new JsonSyntaxException("Entity '" + entityId + "' not found");

            int maxHealth = JsonHelper.getInt(json, "max_health");

            RecipeOutput<Item> output = RecipeOutputImpl.fromJsonRegistry(Registry.ITEM, JsonHelper.getObject(json, "output"));

            return new VivisectionRecipe(id, type, maxHealth, output);
        }

        @Override
        public VivisectionRecipe read(Identifier id, PacketByteBuf buf)
        {
            EntityType<?> type = buf.readRegistryValue(Registry.ENTITY_TYPE);
            int maxHealth = buf.readVarInt();
            RecipeOutput<Item> output = RecipeOutputImpl.fromBuffer(Registry.ITEM, buf);
            return new VivisectionRecipe(id, type, maxHealth, output);
        }

        @Override
        public void write(PacketByteBuf buf, VivisectionRecipe recipe)
        {
            buf.writeRegistryValue(Registry.ENTITY_TYPE, recipe.entityType);
            buf.writeVarInt(recipe.maxHealth);
            recipe.output.write(Registry.ITEM, buf);
        }
    }

    public static class VivisectionContext
    {
        private final World world;
        private final LivingEntity entity;
        private final Vec3d pos;

        public VivisectionContext(World world, LivingEntity entity)
        {
            this.world = world;
            this.entity = entity;
            pos = entity.getPos().add(0, 0.5, 0);
        }
    }
}
