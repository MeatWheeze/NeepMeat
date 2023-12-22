package com.neep.meatlib.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface MeatRecipeSerialiser<T extends MeatRecipe<?>>
{
    T read(Identifier var1, JsonObject var2);

    T read(Identifier id, PacketByteBuf buf);

    void write(PacketByteBuf buf, T recipe);
}
