package com.neep.meatlib.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface MeatRecipeSerialiser<T extends MeatlibRecipe<?>> extends RecipeSerializer<T>
{
    @Override
    T read(Identifier var1, JsonObject var2);

    @Override
    T read(Identifier id, PacketByteBuf buf);

    @Override
    void write(PacketByteBuf buf, T recipe);
}
