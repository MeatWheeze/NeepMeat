package com.neep.meatweapons.mixin;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Box.class)
public interface BoxMixin
{
    @Invoker("traceCollisionSide")
    static Direction traceCollisionSide(Box box, Vec3d intersectingVector, double[] traceDistanceResult, @Nullable Direction approachDirection, double deltaX, double deltaY, double deltaZ)
    {
        throw new IllegalStateException();
    }
}
