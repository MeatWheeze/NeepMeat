package com.neep.meatlib.attachment.player;

public interface MeatPlayerEntity
{
    default PlayerAttachmentManager meatlib$getAttachmentManager() { return null; };
}