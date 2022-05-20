package com.neep.meatweapons.client.model;

import com.neep.meatweapons.entity.BulletEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class BulletEntityModel extends EntityModel<BulletEntity>
{
    private final ModelPart base;

    public BulletEntityModel(ModelPart modelPart)
    {
        base = modelPart.getChild(EntityModelPartNames.CUBE);
    }

    @Override
    public void setAngles(BulletEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
    {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
    {
        // translate model down
        matrices.translate(0, 1.125, 0);

        // render cube
        base.render(matrices, vertices, light, overlay);
    }

    public static TexturedModelData getTexturedModelData()
    {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 0).cuboid(-6F, 12F, -6F, 12F, 12F, 12F), ModelTransform.pivot(0F, 0F, 0F));
        return TexturedModelData.of(modelData, 64, 32);
    }
}
