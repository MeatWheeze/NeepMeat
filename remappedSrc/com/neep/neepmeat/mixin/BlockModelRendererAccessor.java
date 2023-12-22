package com.neep.neepmeat.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.BitSet;

@Mixin(BlockModelRenderer.class)
public interface BlockModelRendererAccessor
{
    @Invoker("getQuadDimensions")
    void callGetQuadDimensions(BlockRenderView world, BlockState state, BlockPos pos, int[] vertexData, Direction face, float[] box, BitSet flags);

    @Invoker("renderQuad")
    void callRenderQuad(BlockRenderView world, BlockState state, BlockPos pos, VertexConsumer vertexConsumer, MatrixStack.Entry matrixEntry, BakedQuad quad, float brightness0, float brightness1, float brightness2, float brightness3, int light0, int light1, int light2, int light3, int overlay);
}
