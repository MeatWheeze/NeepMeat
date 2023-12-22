package com.neep.meatweapons.mixin;

import com.neep.meatweapons.item.BaseGunItem;
import com.neep.meatweapons.item.HeavyCannonItem;
import com.neep.meatweapons.item.IWeakTwoHanded;
import com.neep.neepmeat.item.AnimatedSword;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
    Redirects all calls to ItemStack.areEqual() in updateHeldItems() in order to prevent changes in NBT or damage from
    causing the re-equip animation.
 */

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin
{
    @Shadow private ItemStack mainHand;
    @Shadow private float equipProgressMainHand;
    @Shadow private ItemStack offHand;
    @Shadow private float equipProgressOffHand;

    @Shadow
    private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm)
    {
    }

    @Shadow
    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm)
    {
    }

    @Shadow @Final private EntityRenderDispatcher renderManager;
    private boolean isAiming;
    private float itemXOffset = 0;

    private MinecraftClient client;

    @Inject(method = "updateHeldItems", at = @At(value = "TAIL"))
    public void render(CallbackInfo ci)
    {
        // Using this.client.player returns null in unpredictable circumstances.
//        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
//        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
//        ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
//
//        if (itemStack.getItem() instanceof BaseGunItem && ItemStack.areItemsEqualIgnoreDamage(mainHand, itemStack))
//        {
//            this.equipProgressMainHand = 1;
//            this.mainHand = itemStack;
//        }
//        if (itemStack2.getItem() instanceof BaseGunItem && ItemStack.areItemsEqualIgnoreDamage(offHand, itemStack2))
//        {
//            this.equipProgressOffHand = 1;
//            this.offHand = itemStack2;
//        }
    }

    // Causes offhand arm to render for certain weapons.
    @Inject(method = "renderFirstPersonItem", at = @At(value = "TAIL"))
    public void renderItemHead(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        isAiming = player.isSneaking();
        if (item.getItem() instanceof IWeakTwoHanded || item.getItem() instanceof AnimatedSword)
        {
            // Offhand will only be rendered if empty and not swinging.
            if (hand == Hand.MAIN_HAND && player.getOffHandStack().isEmpty() && !player.handSwinging && !isAiming)
            {
                matrices.push();
                // Change rotation if main hand is left.
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(player.getMainArm() == Arm.RIGHT ? -55 : 55));
                this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, player.getMainArm().getOpposite());
                matrices.pop();
            }
            if (isAiming)
            {
                matrices.push();
                matrices.pop();
            }
        }
        if (item.getItem() instanceof HeavyCannonItem)
        {
            matrices.push();

//            boolean bl = true;
//            float side = bl ? 1.0f : -1.0f;
//            float g = MathHelper.sqrt(swingProgress);
//            ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
//            RenderSystem.setShaderTexture(0, abstractClientPlayerEntity.getSkinTexture());
//
//            matrices.translate(side * -1.0f, 3.6f, 0);
//            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(side * 120.0f));
//            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(200.0f));
//            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(side * -135.0f));
//            matrices.translate(side * 5.6f, 0.5, 0.0);
//
//            ClientPlayerEntity clientPlayer = this.client.player;
//            PlayerEntityRenderer playerRenderer = (PlayerEntityRenderer) renderManager.getRenderer(clientPlayer);
//            playerRenderer.renderRightArm(matrices, vertexConsumers, light, clientPlayer);
            matrices.pop();
        }
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "TAIL"))
    public void renderItemTail(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
    }

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void renderFirstPersonItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        ItemStack itemStack = entity.getStackInHand(Hand.MAIN_HAND);
//        boolean leftHanded = entity.preferredHand == Hand.OFF_HAND;
        if (itemStack.getItem() instanceof BaseGunItem && isAiming)
        {
            itemXOffset = (float) MathHelper.lerp(0.3, itemXOffset, -0.34);
            Vec3f translation = new Vec3f(leftHanded ? -itemXOffset : itemXOffset, 0, 0);
//            translation.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getYaw()));
//            matrices.translate(translation.getX(), translation.getY(), translation.getZ());
        }
        else
        {
            itemXOffset = (float) MathHelper.lerp(0.3, itemXOffset, 0);
        }
    }

}
