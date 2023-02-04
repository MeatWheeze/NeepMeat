package com.neep.meatlib.graphics.client;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.graphics.GraphicsEffect;
import com.neep.meatlib.graphics.GraphicsEffectType;
import com.neep.meatlib.graphics.GraphicsEffects;
import com.neep.neepmeat.NeepMeat;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Environment(value= EnvType.CLIENT)
public class GraphicsEffectClient
{
    public static List<GraphicsEffect> EFFECTS = new ArrayList<>();
    private static Int2ObjectMap<GraphicsEffectFactory> FACTORIES = new Int2ObjectOpenHashMap<>();

    public static void addEffect(GraphicsEffect effect)
    {
        EFFECTS.add(effect);
    }

    public static void init()
    {
        ClientTickEvents.END_WORLD_TICK.register(ctx ->
        {
            ClientWorld world = MinecraftClient.getInstance().world;
            EFFECTS.removeIf(effect -> effect.isRemoved() || effect.getWorld() != world);
            EFFECTS.forEach(GraphicsEffect::tick);
        });

        WorldRenderEvents.AFTER_ENTITIES.register(ctx ->
        {
            MatrixStack matrices = ctx.matrixStack();
            VertexConsumerProvider consumers = ctx.consumers();

            EFFECTS.forEach(((effect) -> effect.render(ctx.camera(), matrices, consumers, ctx.tickDelta())));
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(ctx ->
        {
            MatrixStack matrices = ctx.matrixStack();
            VertexConsumerProvider consumers = ctx.consumers();

            EFFECTS.forEach(((effect) -> effect.renderAfter(ctx.camera(), matrices, consumers, ctx.tickDelta())));
        });

        ClientPlayNetworking.registerGlobalReceiver(GraphicsEffects.CHANNEL_ID, (client, handler, byteBuf, responseSender) ->
        {
            // Obtain the factory, UUID and world ID from the packet before passing it to the newly created GraphicsEffect
    //            GraphicsEffectType type = GraphicsEffects.GRAPHICS_EFFECTS.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            Identifier worldId = byteBuf.readIdentifier();
            int rawId = byteBuf.readVarInt();


            // The buffer must be copied to avoid a reference count error
            PacketByteBuf copiedBuf = PacketByteBufs.copy(byteBuf);
            client.execute(() ->
            {
                ClientWorld world;
                if ((world = MinecraftClient.getInstance().world) == null)
                    throw new IllegalStateException("Tried to spawn effect in a null world!");

                if (world.getRegistryKey().getValue().equals(worldId))
                {
                    MeatLib.LOGGER.info("Spawning graphics effect with uuid " + uuid + " in world " + worldId + ".");
                    GraphicsEffect effect = FACTORIES.get(rawId).create(world, uuid, (PacketByteBuf) copiedBuf);
                    GraphicsEffectClient.addEffect(effect);
                }
            });
        });

    }

    public static GraphicsEffectFactory registerEffect(GraphicsEffectType type, GraphicsEffectFactory factory)
    {
        FACTORIES.put(GraphicsEffects.GRAPHICS_EFFECTS.getRawId(type), factory);
        return factory;
    }
}
