package com.neep.meatlib.recipe.ingredient;

import com.google.gson.JsonObject;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.NotImplementedException;

public class RecipeInputs
{
    public static final Registry<RecipeInput.Serialiser<?>> SERIALISERS = FabricRegistryBuilder.createDefaulted(
            (Class<RecipeInput.Serialiser<?>>) (Object) RecipeInput.Serialiser.class,
            new Identifier(NeepMeat.NAMESPACE, "recipe_input"),
            new Identifier(NeepMeat.NAMESPACE, "null")).buildAndRegister();

    public static final Identifier FLUID_ID = Registry.FLUID.getKey().getValue();
    public static final Identifier ITEM_ID = Registry.ITEM.getKey().getValue();

    public static final RecipeInput.Serialiser<Fluid> FLUID = Registry.register(SERIALISERS, FLUID_ID, new RecipeInput.RegistrySerialiser<>(Registry.FLUID));
    public static final RecipeInput.Serialiser<Item> ITEM = Registry.register(SERIALISERS, ITEM_ID, new RecipeInput.RegistrySerialiser<>(Registry.ITEM));

    public static final RecipeInput<Object> EMPTY = new RecipeInput<>(RecipeInput.Entry.EMPTY, 0, new RecipeInput.Serialiser<>()
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
    }, new Identifier("neepmeat:empty"));
}