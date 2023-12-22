package com.neep.neepmeat.network;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.AnimationSyncable;
import com.neep.neepmeat.entity.bovine_horror.BHBeamGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class EntityAnimationS2C
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "entity_animation");

    public static void send(ServerPlayerEntity player, Entity entity, String animation)
    {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeVarInt(entity.getId());
        buf.writeString(animation);

        ServerPlayNetworking.send(player, ID, buf);
    }

    @Environment(value= EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) ->
            {
                if (client.world == null)
                    return;

                int entityId = buf.readVarInt();
                Entity entity = client.world.getEntityById(entityId);
                String name = buf.readString();

                if (entity instanceof AnimationSyncable animationSyncable)
                {
                    animationSyncable.getQueue().add(name);
                }
            });
        }
    }
}
