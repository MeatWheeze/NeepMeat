package com.neep.meatweapons.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public interface HookableEntity
{
    void meatweapons$setHookParent(@Nullable Entity entity, Vec3d initialOffset);

    void meatweapons$setHookTicks(int ticks);
}
