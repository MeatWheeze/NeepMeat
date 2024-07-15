package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.client.meatgun.RecoilManager;
import com.neep.meatweapons.client.meatgun.animation.MeatgunAnimation;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.component.MeatgunComponent;
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
    protected void renderInner(ItemStack stack, AbstractClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vcp, boolean mainHand, boolean leftHanded, float tickDelta, int light, int overlay)
    {
        if (mode.isFirstPerson())
        {
            MeatgunComponent component = MWComponents.MEATGUN.getNullable(stack);
            if (component != null)
            {
                MeatgunAnimation animation = component.getAnimationManager().getActive();
                animation.applyRender(matrices, tickDelta);
            }

        }
        super.renderInner(stack, player, playerEntityRenderer, mode, matrices, vcp, mainHand, leftHanded, tickDelta, light, overlay);
    }

    @Override
    protected void renderArms(MatrixStack matrices, ModelTransformationMode mode, AbstractClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, VertexConsumerProvider vcp, int light, boolean mainHand, boolean leftHanded)
    {
        super.renderArms(matrices, mode, player, playerEntityRenderer, vcp, light, mainHand, leftHanded);
    }

    @Override
    protected void transformRecoil(MatrixStack matrices, RecoilManager recoil)
    {
        matrices.translate(0, 0, recoil.horAmount);
        if (recoil.amount != 0)
        {
            matrices.translate(0, 0, 1.4);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(recoil.amount));
            matrices.translate(0, 0, -1.4);
        }
    }
}
