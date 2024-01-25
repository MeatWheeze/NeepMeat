package com.neep.meatweapons.particle;

import com.neep.meatweapons.client.BeamRenderer;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.phage_ray.PhageRayEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public class PhageRayGraphicsEffect extends BeamGraphicsEffect
{
    public static final Identifier TRAIL_TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/misc/phage_ray.png");
    public static final RenderLayer TRAIL_LAYER = RenderLayer.getEntityTranslucent(TRAIL_TEXTURE);
    public static final RenderLayer CRYSTAL_BEAM_LAYER = RenderLayer.getEntityTranslucentEmissive(new Identifier(NeepMeat.NAMESPACE, "textures/block/phage_ray/beam.png"));

    private PhageRayEntity parent = null;

    public PhageRayGraphicsEffect(World world, UUID uuid, PacketByteBuf buf)
    {
        super(world, uuid, buf);
        if (world.getEntityById(buf.readVarInt()) instanceof PhageRayEntity phageRay)
        {
            this.parent = phageRay;
        }
        else
            remove();
    }

    @Override
    public void tick()
    {
        super.tick();
    }

    @Override
    public void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers, float tickDelta)
    {
        if (!parent.trigger())
            return;

        this.start = parent.getBeamOrigin();
        this.end = parent.getClientBeamEnd(tickDelta);

        matrices.push();
        VertexConsumer consumer = consumers.getBuffer(TRAIL_LAYER);


        BeamRenderer.renderBeam(matrices, consumer, camera.getPos(),
                start, end, (MathHelper.wrapDegrees(world.getTime()) + tickDelta) * 30, 255, 255, 255,
               255, scale, 255);

        BeamRenderer.renderTube(camera.getPos(), start, end, tickDelta, parent.age, matrices, consumers.getBuffer(CRYSTAL_BEAM_LAYER), 255);

        matrices.pop();
    }
}
