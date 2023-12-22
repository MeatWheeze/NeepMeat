package com.neep.meatweapons.particle;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.BeamRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BulletTrailEffect extends BeamEffect
{
    public static final Identifier TRAIL_TEXTURE = new Identifier(MeatWeapons.NAMESPACE, "textures/misc/bullet_trail.png");
    public static final RenderLayer TRAIL_LAYER = RenderLayer.getEntityTranslucent(TRAIL_TEXTURE);

    public BulletTrailEffect(ClientWorld world, Vec3d start, Vec3d end, Vec3d velocity, float scale, int maxTime)
    {
        super(world, start, end, velocity, scale, maxTime);
    }

    @Override
    public void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta)
    {
        matrices.push();
        VertexConsumer consumer = consumers.getBuffer(TRAIL_LAYER);
        float x = (maxTime - time + 2 - tickDelta) / (float) maxTime;
        BeamRenderer.renderBeam(matrices, consumer, camera.getPos(),
                start, end, 214, 175, 32,
               255, scale);
        matrices.pop();
    }
}
