package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.client.MWClient;
import com.neep.meatweapons.client.model.PlasmaEntityModel;
import com.neep.meatweapons.entity.PlasmaProjectileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class PlasmaEntityRenderer extends EntityRenderer<PlasmaProjectileEntity>
{
    public EntityModel<PlasmaProjectileEntity> model;
    public PlasmaEntityRenderer(EntityRendererFactory.Context context)
    {
        super(context);
        model = new PlasmaEntityModel(context.getPart(MWClient.MODEL_PLASMA_LAYER));
    }

    @Override
    public void render(PlasmaProjectileEntity plasmaEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i)
    {
        matrixStack.push();
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, plasmaEntity.prevYaw, plasmaEntity.getYaw()) - 90.0F));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, plasmaEntity.prevPitch, plasmaEntity.getPitch())));
        model.render(matrixStack, vertexConsumerProvider.getBuffer(model.getLayer(getTexture(plasmaEntity))), i, 1, 1.0F, 1.0F, 1.0F, 0.5F);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(PlasmaProjectileEntity entity) {
        return new Identifier(MeatWeapons.NAMESPACE, "textures/entity/plasma.png");
    }
}
