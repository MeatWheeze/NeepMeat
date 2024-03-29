package com.neep.meatlib.attachment.player;

import net.minecraft.entity.player.PlayerEntity;

public interface PlayerAttachment
{
    void tickAttachment();

    @FunctionalInterface
    interface Factory
    {
        PlayerAttachment create(PlayerEntity player);
    }
}
