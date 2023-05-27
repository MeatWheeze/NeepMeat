package com.neep.meatlib.attachment.player;

public interface MeatPlayerEntity
{
    default PlayerAttachmentManager neepmeat$getAttachmentManager() { return null; };
}