package com.neep.neepmeat.item;

public enum AttackTypes
{
    STANDARD_SWING("swing"),
    STAB("stab"),
    SPECIAL("special");

    public final String animationId;

    AttackTypes(String animationId)
    {
        this.animationId = animationId;
    }
}
