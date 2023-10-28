package com.neep.neepmeat.machine.synthesiser;

import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value = EnvType.CLIENT)
public class SynthesiserRenderer implements BlockEntityRenderer<SynthesiserBlockEntity>
{
    public SynthesiserRenderer(BlockEntityRendererFactory.Context ctx)
    {

    }

    @Override
    public void render(SynthesiserBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
//        float displacement = be.maxProgress != 0 ? MathHelper.lerp(be.progress / be.maxProgress, SynthesiserBlockEntity.MAX_DISPLACEMENT, SynthesiserBlockEntity.MIN_DISPLACEMENT)
//            : SynthesiserBlockEntity.MAX_DISPLACEMENT;
        be.clientDisplacement = MathHelper.lerp(0.1f, be.clientDisplacement, be.getClientDisplacement());
        matrices.translate(0, be.clientDisplacement, 0);
        BERenderUtils.renderModel(NMExtraModels.SYNTHESISER_PLUNGER, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);
    }
}
