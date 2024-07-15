package com.neep.meatweapons.entity;

public interface HitOnCollideEntity
{
    default void meatweapons$setActiveTicks(int ticks) {}

    default void meatweapons$setDamage(float damage) {}
}
