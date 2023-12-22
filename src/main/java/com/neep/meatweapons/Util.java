package com.neep.meatweapons;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Util
{
    public static List<EntityHitResult> getRayTargets(LivingEntity caster, Vec3d startPos, Vec3d endPos, Predicate<Entity> predicate, double margin)
    {
        World world = caster.world;

        Box box = caster.getBoundingBox().stretch(endPos.subtract(startPos)).expand(1.0, 1.0, 1.0);

        // Remove entities not intersecting with the ray
        List<EntityHitResult> list = new ArrayList<>();
        world.getOtherEntities(caster, box, predicate).forEach(entity ->
        {
            Optional<Vec3d> optional = entity.getBoundingBox().expand(entity.getTargetingMargin() + margin).raycast(startPos, endPos);
            optional.ifPresent(vec3d -> list.add(new EntityHitResult(entity, vec3d)));
        });

        return list;
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
