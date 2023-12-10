package com.neep.meatlib.network;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.recipe.MeatRecipe;
import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.RecipeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class SyncMeatRecipesS2CPacket
{
    public static final Identifier ID = new Identifier(MeatLib.NAMESPACE, "recipe_sync");

    public static void send(ServerPlayerEntity player, Collection<MeatRecipe<?>> recipes)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeCollection(recipes, SyncMeatRecipesS2CPacket::writeRecipe);

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static MeatRecipe<?> readRecipe(PacketByteBuf buf)
    {
        Identifier identifier = buf.readIdentifier();
        Identifier identifier2 = buf.readIdentifier();
        return RecipeRegistry.RECIPE_SERIALISER.getOrEmpty(identifier).orElseThrow(() -> new IllegalArgumentException("Unknown special recipe serializer " + identifier)).read(identifier2, buf);
    }

    public static <T extends MeatRecipe<?>> void writeRecipe(PacketByteBuf buf, T recipe)
    {
        MeatRecipeSerialiser<T> serialiser = (MeatRecipeSerialiser<T>) recipe.getSerialiser();
        buf.writeIdentifier(RecipeRegistry.RECIPE_SERIALISER.getId(serialiser));
        buf.writeIdentifier(recipe.getId());
        serialiser.write(buf, recipe);
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) ->
            {
                Collection<MeatRecipe<?>> recipes = buf.readList(SyncMeatRecipesS2CPacket::readRecipe);

                // We do not need to set the recipes on an integrated server
//                if (client.getServer() != null)
//                {
//                    return;
//                }

                MeatRecipeManager.getInstance().setRecipes(recipes);
            });
        }
    }
}
