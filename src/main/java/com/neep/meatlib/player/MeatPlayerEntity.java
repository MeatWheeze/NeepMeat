package com.neep.meatlib.player;

public interface MeatPlayerEntity
{
    default PlayerAttachmentManager neepmeat$getAttachmentManager() { return null; };
}