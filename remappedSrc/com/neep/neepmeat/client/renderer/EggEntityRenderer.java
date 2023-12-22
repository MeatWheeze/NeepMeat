package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.entity.EggEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value = EnvType.CLIENT)
public class EggEntityRenderer extends EntityRenderer<EggEntity>
{
    public EggEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer)
    {
        super(ctx);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(EggEntity entity, float f, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i)
    {
        float b = tickDelta / 20;
        matrices.push();

        float p = (float) entity.getWobbleTicks() - tickDelta;
        float q = entity.getWobbleStrength() - tickDelta;
        if (q < 0.0f)
        {
            q = 0.0f;
        }
        if (p > 0.0f)
        {
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(p) * p * q / 10.0f * entity.getWobbleDirection()));
        }

        matrices.translate(-0.5, 0, -0.5);
        BERenderUtils.renderModel(NMExtraModels.EGG, matrices, entity.getWorld(), entity.getBlockPos(), Blocks.STONE.getDefaultState(), vertexConsumerProvider);
        matrices.pop();
        super.render(entity, f, b, matrices, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(EggEntity entity)
    {
        return new Identifier(NeepMeat.NAMESPACE, "textures/entity/glome.png");
    }
}
