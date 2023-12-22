package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.transport.machine.fluid.GlassTankBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class GlassTankRenderer implements BlockEntityRenderer<GlassTankBlockEntity>
{
    private static ItemStack stack = new ItemStack(Items.JUKEBOX, 1);
    Model model;

    public GlassTankRenderer(BlockEntityRendererFactory.Context context)
    {
        model = new GlassTankModel(context.getLayerModelPart(NeepMeatClient.MODEL_GLASS_TANK_LAYER));
    }

    @Override
    public void render(GlassTankBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        WritableSingleFluidStorage buffer = blockEntity.getStorage(null);
        float scale = ((float) buffer.getAmount()) / ((float) buffer.getCapacity());
        buffer.renderLevel = MathHelper.lerp(0.1f, buffer.renderLevel,(buffer.getAmount()) / (float) buffer.getCapacity());
        FluidVariant fluid = blockEntity.getStorage(null).getResource();
//        renderFluidCuboid(vertexConsumers, matrices, fluid, 0.07f, 0.93f, 0.93f, buffer.renderLevel, light);
        BERenderUtils.renderFluidCuboid(vertexConsumers, matrices, fluid, 0.1f, 0.93f, 0.93f, buffer.renderLevel, light);

        matrices.pop();
    }
}