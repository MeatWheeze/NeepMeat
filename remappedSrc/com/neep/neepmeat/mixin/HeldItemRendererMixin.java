package com.neep.neepmeat.mixin;

import com.neep.neepmeat.item.AnimatedSword;
import com.neep.neepmeat.item.AttackTypes;
import com.neep.neepmeat.item.SlasherItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.util.GeckoLibUtil;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin
{
    @Inject(method = "renderFirstPersonItem", at = @At(value = "TAIL"))
    public void renderItemHead(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        if (item.getItem() instanceof AnimatedSword)
        {
            if (player.handSwinging)
            {
//                ((AnimatedSword) item.getItem()).onAnimationSync(GeckoLibUtil.getIDFromStack(item), AnimatedSword.ANIM_SWING);
                player.handSwinging = false;
            }
        }
    }
}
