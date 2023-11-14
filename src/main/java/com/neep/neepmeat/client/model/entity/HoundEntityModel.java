package com.neep.neepmeat.client.model.entity;

import com.neep.neepmeat.entity.hound.HoundEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class HoundEntityModel extends SinglePartEntityModel<HoundEntity>
{

    public static final String MAIN = "main";
    public static final String EXTREMITY_1 = "extremity_1";
    public static final String EXTREMITY_2 = "extremity_2";
    public static final String EXTREMITY_3 = "extremity_3";

    protected ModelPart root;

    public static TexturedModelData getTexturedModelData()
    {
        ModelData modelData = new ModelData();

        ModelPartData root = modelData.getRoot();

        root.addChild(MAIN, ModelPartBuilder.create()
                .uv(0, 0)
//                .cuboid(-8.0F, 0.0F, -8.0F, 16.0F, 32.0F, 16.0F, new Dilation(0.0F)),
                .cuboid(-8F, -8F, -8F, 16F, 32F, 16F),
                ModelTransform.pivot(0.0f, 0.0f, 0.0f));

        return TexturedModelData.of(modelData, 128, 128);
    }

    public HoundEntityModel(ModelPart root)
    {
        super(RenderLayer::getEntityCutoutNoCull);
        this.root = root;
    }

    @Override
    public ModelPart getPart()
    {
        return root;
    }

    @Override
    public void setAngles(HoundEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
    {

    }
}
