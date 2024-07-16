package com.neep.meatweapons;

import com.neep.neepmeat.util.NMMaths;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Util
{
    public static List<EntityHitResult> getRayTargets(LivingEntity caster, Vec3d startPos, Vec3d endPos, Predicate<Entity> predicate, double margin)
    {
        World world = caster.getWorld();

        Box box = caster.getBoundingBox().stretch(endPos.subtract(startPos)).expand(1.0, 1.0, 1.0);

        return world.getOtherEntities(caster, box, predicate)
                .stream()
                .<EntityHitResult>mapMulti((entity, consumer) ->
        {
            @Nullable Vec3d vec3d = NMMaths.raycast(entity.getBoundingBox().expand(entity.getTargetingMargin() + margin), startPos, endPos);
            if (vec3d != null)
                consumer.accept(new EntityHitResult(entity, vec3d));
        }).toList();
    }

    public static Vec3d getRotationVector(float pitch, float yaw)
    {
        float h = MathHelper.cos(-yaw);
        float i = MathHelper.sin(-yaw);
        float j = MathHelper.cos(pitch);
        float k = MathHelper.sin(pitch);
        return new Vec3d(i * j, -k, h * j);
    }
}
