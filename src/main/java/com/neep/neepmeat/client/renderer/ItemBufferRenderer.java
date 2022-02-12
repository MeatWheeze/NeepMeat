package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.NeepMeatClient;
import com.neep.neepmeat.blockentity.GlassTankBlockEntity;
import com.neep.neepmeat.blockentity.ItemBufferBlockEntity;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.fluid_util.FluidBuffer;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class ItemBufferRenderer implements BlockEntityRenderer<ItemBufferBlockEntity>
{
    Model model;
    float currentOffset = 0;

    public ItemBufferRenderer(BlockEntityRendererFactory.Context context)
    {
        model = new GlassTankModel(context.getLayerModelPart(NeepMeatClient.MODEL_GLASS_TANK_LAYER));
    }

    @Override
    public void render(ItemBufferBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        ItemStack stack = blockEntity.getResource().toStack((int) blockEntity.getAmount());

        float delta = 0.01f;

        currentOffset = MathHelper.lerp(delta, currentOffset, blockEntity.getAmount() <= 0 ? 0f : 1f);
        matrices.translate(0.5, 0.25f + currentOffset, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 1));

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);

        matrices.pop();
    }
}