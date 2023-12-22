package com.neep.meatweapons.particle;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.BeamRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class ZapBeamEffect extends BeamGraphicsEffect
{
    public static final Identifier TRAIL_TEXTURE = new Identifier(MeatWeapons.NAMESPACE, "textures/misc/zap_trail.png");
    public static final RenderLayer TRAIL_LAYER = RenderLayer.getEntityTranslucent(TRAIL_TEXTURE);

    public ZapBeamEffect(World world, UUID uuid, PacketByteBuf buf)
    {
        super(world, uuid, buf);
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    public void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta)
    {
        matrices.push();
        VertexConsumer consumer = consumers.getBuffer(TRAIL_LAYER);
        float x = (maxTime - time + 2 - tickDelta) / (float) maxTime;
        float length = 0.5f;
        float distance = (float) end.distanceTo(start);
        Vec3d beam = end.subtract(start).normalize();
        Vec3d newStart = start.add(beam.multiply(distance * x));
        Vec3d newEnd = start.add(beam.multiply((distance * x) + length));
        BeamRenderer.renderBeam(matrices, consumer, camera.getPos(),
                newStart, newEnd, 255, 255, 255,
               255, scale, 255);
        matrices.pop();
    }
}
