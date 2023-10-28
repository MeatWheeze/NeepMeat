package com.neep.meatweapons.client.model;

import com.neep.meatweapons.entity.FusionBlastEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class PlasmaEntityModel extends EntityModel<FusionBlastEntity>
{
	private final ModelPart bb_main;

	public PlasmaEntityModel(ModelPart modelPart)
	{
//		textureWidth = 32;
//		textureHeight = 32;

//		bb_main = new ModelPart(this);
		bb_main = modelPart.getChild(EntityModelPartNames.CUBE);
//		bb_main.rotate(new MatrixStack().multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(24f)));
//		bb_main.setTextureOffset(0, 0).addCuboid(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
//		bb_main.cub(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
	}

	@Override
	public void setAngles(FusionBlastEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
	{

	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha)
	{
		bb_main.render(matrices, vertices, light, overlay);
	}

	public static TexturedModelData getTexturedModelData()
	{
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 0).cuboid(-1F, -1F, -1F, 2F, 2F, 2F), ModelTransform.pivot(0F, 0F, 0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

}