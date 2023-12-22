package com.neep.neepmeat.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class GlassTankModel extends Model
{
    private final ModelPart bb_main;

    public GlassTankModel(ModelPart root)
    {
        super(RenderLayer::getArmorCutoutNoCull);
        this.bb_main = root.getChild(EntityModelPartNames.CUBE);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        ImmutableList.of(this.bb_main).forEach((modelRenderer) -> {
            modelRenderer.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        });
    }

    public static TexturedModelData getTexturedModelData()
    {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 0).cuboid(0.75f, 0.75F, 0.75f, 14.5F, 14.5F, 14.5f), ModelTransform.pivot(0F, 0F, 0F));

        return TexturedModelData.of(modelData, 32, 32);
    }
}
