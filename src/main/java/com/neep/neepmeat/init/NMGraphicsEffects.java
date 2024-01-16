package com.neep.neepmeat.init;

import com.neep.meatlib.graphics.GraphicsEffectType;
import com.neep.meatlib.graphics.GraphicsEffects;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class NMGraphicsEffects
{
    public static final GraphicsEffectType REMINA = GraphicsEffects.register(NeepMeat.NAMESPACE, "remina", new GraphicsEffectType());
    public static final GraphicsEffectType PHAGE_RAY = GraphicsEffects.register(NeepMeat.NAMESPACE, "phage_ray", new GraphicsEffectType());

    public static void init()
    {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
        {
//            ServerPlayNetworking.send(handler.getPlayer(),GraphicsEffects.CHANNEL_ID, GraphicsEffects.createPacket(REMINA, handler.getPlayer().getWorld()));
        });
    }

}
