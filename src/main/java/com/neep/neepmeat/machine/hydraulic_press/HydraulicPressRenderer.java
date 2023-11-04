package com.neep.neepmeat.machine.hydraulic_press;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(value = EnvType.CLIENT)
public class HydraulicPressRenderer implements BlockEntityRenderer<HydraulicPressBlockEntity>
{
    public static final float MAX_DISPLACEMENT = 8 / 16f + 0.04f;

    public HydraulicPressRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(HydraulicPressBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
//        Direction facing = be.getCachedState().get(HydraulicPressBlock.FACING);
        WritableSingleFluidStorage storage = be.getStorage(null);
        float extension = storage.getAmount() / (float) HydraulicPressBlockEntity.EXTEND_AMOUNT;
        be.renderExtension = MathHelper.lerp(0.1f, be.renderExtension, extension);
//        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0, - MAX_DISPLACEMENT * be.renderExtension, 0);
        BERenderUtils.renderModel(NMExtraModels.HYDRAULIC_PRESS_ARM, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
    }
}
