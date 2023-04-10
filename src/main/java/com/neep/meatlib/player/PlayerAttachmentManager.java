package com.neep.meatlib.player;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.system.CallbackI;

import java.util.Map;

public class PlayerAttachmentManager
{
    private static final Map<String, PlayerAttachment.Factory> FACTORIES = Maps.newHashMap();

    protected final Map<String, PlayerAttachment> attachments = Maps.newHashMap();
    protected final PlayerEntity player;

    public PlayerAttachmentManager(PlayerEntity player)
    {
        this.player = player;
        FACTORIES.forEach((id, f) ->
        {
            attachments.put(id, f.create(player));
        });
    }

    public void tick()
    {
        attachments.values().forEach(PlayerAttachment::tick);
    }

    public <T extends PlayerAttachment> T getAttachment(String id)
    {
        // URrrrrrrrrrr, not sure about this.
        return (T) attachments.get(id);
    }

    protected void addAttachment(String id, PlayerAttachment attachment)
    {
        attachments.put(id, attachment);
    }

    public static void registerAttachment(String id, PlayerAttachment.Factory factory)
    {
        FACTORIES.put(id, factory);
    }

    public static void init()
    {
//        ServerPlayConnectionEvents.INIT.register((handler, server) ->
//        {
//            handler.getPlayer().neepmeat$getAttachmentManager().
//        });
    }
}
