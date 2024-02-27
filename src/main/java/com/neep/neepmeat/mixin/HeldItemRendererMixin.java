package com.neep.neepmeat.mixin;

import com.neep.meatweapons.item.AssaultDrillItem;
import com.neep.neepmeat.api.item.OverrideSwingItem;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin
{
    @Shadow private float equipProgressMainHand;

    @Shadow private ItemStack mainHand;

    @Inject(method = "renderFirstPersonItem", at = @At(value = "TAIL"))
    public void renderItemHead(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        if (stack.getItem() instanceof OverrideSwingItem item && stack.equals(player.getMainHandStack()))
        {
            if (player.handSwinging)
            {
                player.handSwinging = item.onSwing(stack, player);
            }
        }
    }

    @Inject(method = "updateHeldItems", at = @At(value = "TAIL"))
    public void render(CallbackInfo ci)
    {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        ItemStack itemStack = clientPlayer.getMainHandStack();

        if (itemStack.getItem() instanceof AssaultDrillItem && ItemStack.areItemsEqual(mainHand, itemStack))
        {
            this.equipProgressMainHand = 1;
            this.mainHand = itemStack;
        }
    }

    @Inject(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "HEAD"), cancellable = true)
    public void onRenderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci)
    {
        if (PLCHudRenderer.active())
            ci.cancel();
    }
}
