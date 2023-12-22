package com.neep.meatlib.recipe.ingredient;

import com.google.gson.JsonObject;
import com.neep.meatlib.MeatLib;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.NotImplementedException;

public class RecipeInputs
{
    public static final Identifier EMPTY_ID = new Identifier(MeatLib.NAMESPACE, "empty");
    public static final Registry<RecipeInput.Serialiser<?>> SERIALISERS = FabricRegistryBuilder.createDefaulted(
            (Class<RecipeInput.Serialiser<?>>) (Object) RecipeInput.Serialiser.class,
            new Identifier(MeatLib.NAMESPACE, "recipe_input"),
            EMPTY_ID).buildAndRegister();

    public static final Identifier FLUID_ID = Registry.FLUID.getKey().getValue();
    public static final Identifier ITEM_ID = Registry.ITEM.getKey().getValue();
    public static final Identifier ENTITY_MUTATE_ID = Registry.ENTITY_TYPE.getKey().getValue();

    public static final RecipeInput.Serialiser<Object> EMPTY_SERIALISER = Registry.register(SERIALISERS, EMPTY_ID, new RecipeInput.Serialiser<>()
    {
        @Override
        public RecipeInput<Object> fromJson(JsonObject json)
        {
            return EMPTY;
        }

        @Override
        public RecipeInput<Object> fromBuffer(PacketByteBuf buf)
        {
            return EMPTY;
        }

        @Override
        public void write(PacketByteBuf buf, RecipeInput<Object> input)
        {
            buf.writeString(input.getType().toString());
        }

        @Override
        public Object getObject(Identifier id)
        {
            throw new NotImplementedException();
        }

        @Override
        public Identifier getId(Object o)
        {
            throw new NotImplementedException();
        }
    });

    public static final RecipeInput.Serialiser<Fluid> FLUID = Registry.register(SERIALISERS, FLUID_ID, new RecipeInput.RegistrySerialiser<>(Registry.FLUID));
    public static final RecipeInput.Serialiser<Item> ITEM = Registry.register(SERIALISERS, ITEM_ID, new RecipeInput.RegistrySerialiser<>(Registry.ITEM));
    public static final RecipeInput.Serialiser<EntityType<?>> ENTITY = Registry.register(SERIALISERS, ENTITY_MUTATE_ID, new RecipeInput.RegistrySerialiser<>(Registry.ENTITY_TYPE));

    public static final RecipeInput<Object> EMPTY = new RecipeInput<>(RecipeInput.Entry.EMPTY, 0, EMPTY_SERIALISER, EMPTY_ID);
}
