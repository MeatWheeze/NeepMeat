package com.neep.meatlib.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface MeatRecipeSerialiser<T extends MeatRecipe<?>>
{
    T read(Identifier var1, JsonObject var2);

    /**
     * Reads a recipe from a packet byte buf, usually on the client.
     *
     * <p>This can throw whatever exception the packet byte buf throws. This may be
     * called in the netty event loop than the client game engine thread.
     *
     * @return the read recipe
     *
     * @param buf the recipe buf
     * @param id the recipe's ID
     */
    T read(Identifier id, PacketByteBuf buf);

    /**
     * Writes a recipe to a packet byte buf, usually on the server.
     *
     * <p>The recipe's ID is already written into the buf when this is called.
     *
     * <p>This can throw whatever exception the packet byte buf throws. This may be
     * called in the netty event loop than the server game engine thread.
     *
     * @param buf the recipe buf
     * @param recipe the recipe
     */
    void write(PacketByteBuf buf, T recipe);
}
