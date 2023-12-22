package com.neep.meatlib.graphics;

import com.neep.meatlib.MeatLib;
import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;

public class GraphicsEffects
{
    public static void init()
    {

    }

    public static final Identifier CHANNEL_ID = new Identifier(MeatLib.NAMESPACE, "graphics_effect");
    public static final Identifier BLANK_EFFECT = new Identifier(MeatLib.NAMESPACE, "empty");

    public static DefaultedRegistry<GraphicsEffectType> GRAPHICS_EFFECTS = FabricRegistryBuilder.createDefaulted(GraphicsEffectType.class,
            new Identifier(MeatWeapons.NAMESPACE, "graphics_effects"),
            BLANK_EFFECT).buildAndRegister();

//    public static final GraphicsEffectFactory BLANK_FACTORY = l

    public static GraphicsEffectType register(String namespace, String id, GraphicsEffectType factory)
    {
        return Registry.register(GRAPHICS_EFFECTS, new Identifier(namespace, id), factory);
    }

    /**
     * @return A PacketByteBuf containing UUID and registry ID.
     */
    public static PacketByteBuf createPacket(GraphicsEffectType factory, World world)
    {
        UUID uuid = UUID.randomUUID();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeUuid(uuid);
        buf.writeIdentifier(world.getRegistryKey().getValue());
        buf.writeVarInt(GRAPHICS_EFFECTS.getRawId(factory));

        return buf;
    }
}
