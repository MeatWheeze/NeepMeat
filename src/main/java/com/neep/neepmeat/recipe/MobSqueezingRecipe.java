package com.neep.neepmeat.recipe;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.recipe.ingredient.RecipeOutputImpl;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.hydraulic_press.MobSqueezeContext;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FluidDrainBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class MobSqueezingRecipe extends ImplementedRecipe<MobSqueezeContext>
{
    protected final Identifier id;
    protected final EntityType<? extends Entity> entityType;
    protected final RecipeOutputImpl<Fluid> fluidOutput;

    public MobSqueezingRecipe(Identifier id, EntityType<? extends Entity> entityType, RecipeOutputImpl<Fluid> fluidOutput)
    {
        this.entityType = entityType;
        this.fluidOutput = fluidOutput;
        this.id = id;
    }

    @Override
    public boolean matches(MobSqueezeContext context, World world)
    {
        Box box = Box.from(new BlockBox(context.getPos().down()));
        List<Entity> entities = world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), box, e -> Objects.equals(e.getType(), entityType));
        return entities.size() > 0;
    }

    public EntityType<? extends Entity> getEntityType()
    {
        return entityType;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return NMrecipeTypes.MOB_SQUEEZING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.MOB_SQUEEZING;
    }

    public void finishRecipe(MobSqueezeContext context, World world)
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            if (world.getBlockEntity(context.getPos().down(2)) instanceof FluidDrainBlockEntity be)
            {
                fluidOutput.insertInto(be.getBuffer(null), FluidVariant::of, transaction);
            }
            transaction.commit();
        }
        world.playSound(null, context.getPos().getX(), context.getPos().getY(), context.getPos().getZ(),
                SoundEvents.ENTITY_COW_HURT,
                SoundCategory.AMBIENT,
                1, 1);

        if (world instanceof ServerWorld serverWorld)
        {
            serverWorld.spawnParticles(NMParticles.MILK_SPLASH,
                    context.getPos().getX() + 0.5, context.getPos().getY() + 0.5, context.getPos().getZ() + 0.5,
                    10, 0.25, 0.25, 0.25, 0.0001);
        }
    }

    public static class Serializer implements RecipeSerializer<MobSqueezingRecipe>
    {
        RecipeFactory<MobSqueezingRecipe> factory;

        public Serializer(RecipeFactory<MobSqueezingRecipe> recipeFactory)
        {
            this.factory = recipeFactory;
        }

        @Override
        public MobSqueezingRecipe read(Identifier id, JsonObject json)
        {
            JsonObject entityElement = JsonHelper.getObject(json, "entity");
            Identifier entityId = new Identifier(JsonHelper.getString(entityElement, "id"));
            EntityType<? extends Entity> entityType = Registries.ENTITY_TYPE.get(entityId);

            JsonObject itemOutputElement = JsonHelper.getObject(json, "output");
            RecipeOutputImpl<Fluid> itemOutput = RecipeOutputImpl.fromJsonRegistry(Registries.FLUID, itemOutputElement);

            return this.factory.create(id, entityType, itemOutput);
        }

        @Override
        public MobSqueezingRecipe read(Identifier id, PacketByteBuf buf)
        {
//            RecipeInput<Fluid> fluidInput = RecipeInput.fromBuffer(Registries.FLUID, buf);
            Identifier entityId = buf.readIdentifier();
            EntityType<? extends Entity> entityType = Registries.ENTITY_TYPE.get(entityId);
            RecipeOutputImpl<Fluid> itemOutput = RecipeOutputImpl.fromBuffer(Registries.FLUID, buf);

            return this.factory.create(id, entityType, itemOutput);
        }

        @Override
        public void write(PacketByteBuf buf, MobSqueezingRecipe recipe)
        {
            buf.writeIdentifier(Registries.ENTITY_TYPE.getId(recipe.entityType));
            recipe.fluidOutput.write(Registries.FLUID, buf);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends MobSqueezingRecipe>
        {
            T create(Identifier var1, EntityType<? extends Entity> entityType, RecipeOutputImpl<Fluid> out);
        }
    }
}
