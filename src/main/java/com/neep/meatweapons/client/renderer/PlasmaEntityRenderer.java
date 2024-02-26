package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.MWClient;
import com.neep.meatweapons.client.model.PlasmaEntityModel;
import com.neep.meatweapons.entity.FusionBlastEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vector3f;

public class PlasmaEntityRenderer extends EntityRenderer<FusionBlastEntity>
{
    public EntityModel<FusionBlastEntity> model;
    public PlasmaEntityRenderer(EntityRendererFactory.Context context)
    {
        super(context);
        model = new PlasmaEntityModel(context.getPart(MWClient.MODEL_PLASMA_LAYER));
    }

    protected final float MIN_SCALE = 2f;
    protected final float MAX_SCALE = 6f;

    @Override
    public void render(FusionBlastEntity plasmaEntity, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light)
    {
        float angle = (plasmaEntity.age + tickDelta) * 30;
        float scale = MathHelper.lerp(plasmaEntity.getPower() / FusionBlastEntity.MAX_POWER, MIN_SCALE, MAX_SCALE);

        matrixStack.push();
        matrixStack.scale(scale, scale , scale);

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(angle));
//        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, plasmaEntity.prevYaw, plasmaEntity.getYaw()) - 90.0F));
//        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, plasmaEntity.prevPitch, plasmaEntity.getPitch())));
        model.render(matrixStack, vertexConsumerProvider.getBuffer(model.getLayer(getTexture(plasmaEntity))), light, 1, 1.0F, 1.0F, 1.0F, 0.5F);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(FusionBlastEntity entity) {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/entity/plasma.png");
    }
}
