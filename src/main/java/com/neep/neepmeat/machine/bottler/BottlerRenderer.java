package com.neep.neepmeat.machine.bottler;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value = EnvType.CLIENT)
public class BottlerRenderer implements BlockEntityRenderer<BottlerBlockEntity>
{
    public BottlerRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(BottlerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(BottlerBlock.FACING);
        ItemStack stack = be.getItemStorage(null).getAsStack();

        matrices.push();
        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0.5, 0.5, 0.15);
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, null, 0);
        matrices.pop();

        float timeDiff = be.getWorld().getTime() + tickDelta - be.getStartTime();
        float delta = timeDiff / ((float) be.getMaxProgress() / be.progressIncrement());
        float yOffset = Math.min(0, MathHelper.lerp(delta, -8 / 16f, 0));
        matrices.translate(0, yOffset, 0);
        BERenderUtils.renderModel(NMExtraModels.PUMP, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
    }
}
