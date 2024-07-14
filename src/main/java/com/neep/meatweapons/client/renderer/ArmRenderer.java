package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.item.meatgun.MeatgunItem;
import com.neep.meatweapons.item.WeakTwoHanded;
import com.neep.meatweapons.mixin.HeldItemRendererAccessor;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;

public class ArmRenderer
{
    public static void onRenderArm(MatrixStack matrices, AbstractClientPlayerEntity player, ItemStack stack, Hand hand, HeldItemRenderer heldItemRenderer,
                                   float equipProgress, float swingProgress, VertexConsumerProvider vcp, int light)
    {
        boolean isAiming = player.isSneaking();
        if (stack.getItem() instanceof MeatgunItem)
        {
//            if (hand == Hand.MAIN_HAND && player.getOffHandStack().isEmpty() && !player.handSwinging && !isAiming)
//            {
//                matrices.push();
//
//                MeatgunComponent component = MWComponents.MEATGUN.get(stack);
//                RecoilManager recoil = component.getRecoil();
//
//                transformRecoil(matrices, recoil);
//
//                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(player.getMainArm() == Arm.RIGHT ? -55 : 55));
////                ((HeldItemRendererAccessor) heldItemRenderer).callRenderArmHoldingItem(matrices, vcp, light, equipProgress, swingProgress, player.getMainArm().getOpposite());
//                matrices.pop();
//            }
        }
        else if (stack.getItem() instanceof WeakTwoHanded weakTwoHanded && weakTwoHanded.displayArmFirstPerson(player.getStackInHand(hand), hand))
        {
            // Offhand will only be rendered if empty and not swinging.
            if (hand == Hand.MAIN_HAND && player.getOffHandStack().isEmpty() && !player.handSwinging && !isAiming)
            {
                matrices.push();
                // Change rotation if main hand is left.
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(player.getMainArm() == Arm.RIGHT ? -55 : 55));
                ((HeldItemRendererAccessor) heldItemRenderer).callRenderArmHoldingItem(matrices, vcp, light, equipProgress, swingProgress, player.getMainArm().getOpposite());
                matrices.pop();
            }
            if (isAiming)
            {
                matrices.push();
                matrices.pop();
            }
        }

    }
}
