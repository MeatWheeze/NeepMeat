package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.blockentity.pipe.MergePipeBlockEntity;
import com.neep.neepmeat.blockentity.pipe.PneumaticPipeBlockEntity;
import com.neep.neepmeat.util.ItemInPipe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class MergePipeRenderer<T extends MergePipeBlockEntity> extends PneumaticPipeRenderer<T>
{

    public MergePipeRenderer(BlockEntityRendererFactory.Context context)
    {
        super(context);
    }

    @Override
    public void render(T be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        super.render(be, tickDelta, matrices, vertexConsumers, light, overlay);
    }
}