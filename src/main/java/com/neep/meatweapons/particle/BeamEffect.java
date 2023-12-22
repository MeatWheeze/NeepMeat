package com.neep.meatweapons.particle;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.BeamRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@Environment(value= EnvType.CLIENT)
public class BeamEffect extends GraphicsEffect
{
    public static final Identifier TEX_TEST = new Identifier(MeatWeapons.NAMESPACE, "textures/misc/beam.png");
    public static final RenderLayer LAYER_TEST = RenderLayer.getEntityTranslucent(TEX_TEST);

    protected Vec3d startPos;
    protected Vec3d endPos;
    public long maxTime;

    public BeamEffect(ClientWorld world, Vec3d start, Vec3d end, Vec3d velocity, int maxTime)
    {
        super(world);
        this.maxTime = maxTime;
        this.startPos = start;
        this.endPos = end;
        this.world = world;
    }

    public void tick()
    {
        super.tick();

        if (maxTime > 0 && time > maxTime)
        {
            this.remove();
        }
    }

    @Override
    public void render(Camera camera, MatrixStack matrices, VertexConsumerProvider consumers)
    {
        matrices.push();
        VertexConsumer consumer = consumers.getBuffer(LAYER_TEST);
        BeamRenderer.renderBeam(matrices, consumer, camera.getPos(), startPos, endPos, 255, 255, 255,
//                maxTime > 0 ? (int) (255f * (maxTime - time) / maxTime) : 255, 0.5f);
                maxTime > 0 ? (int) (255f * (maxTime - time + 1) / maxTime) : 255, 0.5f);
        matrices.pop();
    }

    public void remove()
    {
        this.alive = false;
    }

}
