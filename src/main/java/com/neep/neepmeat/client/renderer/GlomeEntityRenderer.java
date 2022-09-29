package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GlomeEntityModel;
import com.neep.neepmeat.entity.GlomeEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class GlomeEntityRenderer extends LivingEntityRenderer<GlomeEntity, GlomeEntityModel>
{
    public GlomeEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer)
    {
        super(ctx, new GlomeEntityModel(ctx.getPart(layer)), 0.3f);
    }

    @Override
    public void render(GlomeEntity livingEntity, float f, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i)
    {
        float a = livingEntity.getLifeTime() / 20f;
        float b = tickDelta / 20;
        float sin = (float) (Math.sin(a) * Math.cos(b) + Math.cos(a) * Math.sin(b));
        float scale = MathHelper.lerp((sin + 1f) / 2f, 0.2f, 1f);
        matrices.scale(scale, scale, scale);
        super.render(livingEntity, f, b, matrices, vertexConsumerProvider, i);
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(GlomeEntity entity, boolean showBody, boolean translucent, boolean showOutline)
    {
        return RenderLayer.getEntityTranslucent(getTexture(entity));
    }

    @Override
    public Identifier getTexture(GlomeEntity entity)
    {
        return new Identifier(NeepMeat.NAMESPACE, "textures/entity/glome.png");
    }

    @Override
    protected void renderLabelIfPresent(GlomeEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
//        super.renderLabelIfPresent(entity, text, matrices, vertexConsumers, light);
    }
}
