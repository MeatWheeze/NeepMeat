package com.neep.meatweapons.client.renderer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class MeatgunStaffRenderer extends MeatgunPistolRenderer
{
    @Override
    protected void renderInner(ItemStack stack, AbstractClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vcp, boolean mainHand, boolean leftHanded, int light, int overlay)
    {
        if (mode.isFirstPerson())
        {
            matrices.translate(0, -4 / 16f, 0 / 16f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-0));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(5));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20));
        }
        super.renderInner(stack, player, playerEntityRenderer, mode, matrices, vcp, mainHand, leftHanded, light, overlay);
    }

    @Override
    protected void renderArms(MatrixStack matrices, ModelTransformationMode mode, AbstractClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, VertexConsumerProvider vcp, int light, boolean mainHand, boolean leftHanded)
    {
        super.renderArms(matrices, mode, player, playerEntityRenderer, vcp, light, mainHand, leftHanded);
    }
}
