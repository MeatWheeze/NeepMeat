package com.neep.neepmeat.mixin;

import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressBlockEntity;
import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntityRenderer.class)
public class MixinCowEntityRenderer
{
    @Inject(method = "render*", at = @At(value = "HEAD"))
    public <T extends Entity> void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci)
    {
        BlockPos pos = entity.getBlockPos().up();
        World world = entity.getWorld();
        if (entity instanceof CowEntity && world.getBlockEntity(pos) instanceof HydraulicPressBlockEntity be)
        {
            float height = entity.getHeight();
            float scale = (height - be.renderExtension * HydraulicPressRenderer.MAX_DISPLACEMENT) / height;
            matrices.scale(1, scale, 1);
        }
    }
}
