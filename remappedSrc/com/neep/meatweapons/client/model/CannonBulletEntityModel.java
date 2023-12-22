package com.neep.meatweapons.client.model;

import com.neep.meatweapons.entity.CannonBulletEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

public class CannonBulletEntityModel extends EntityModel<CannonBulletEntity>
{
	private final ModelPart bb_main;

	public CannonBulletEntityModel(ModelPart modelPart)
	{
		int textureWidth = 32;
		int textureHeight = 32;

		bb_main = modelPart.getChild(EntityModelPartNames.CUBE);
//		bb_main = new ModelPart(this);
//		bb_main.setTextureOffset(0, 0).addCuboid(-1.5F, -3.0F, 0.0F, 3.0F, 3.0F, 3.0F, 0.0F, false);
//		bb_main.setTextureOffset(0, 0).addCuboid(-0.5F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setAngles(CannonBulletEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch)
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
		modelPartData.addChild(EntityModelPartNames.CUBE, ModelPartBuilder.create().uv(0, 0).cuboid(-2F, -2F, -2F, 2F, 2F, 2F), ModelTransform.pivot(0F, 0F, 0F));
		return TexturedModelData.of(modelData, 64, 32);
	}
}