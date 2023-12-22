package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.blockentity.ItemBufferBlockEntity;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.storage.WritableStackStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@SuppressWarnings("UnstableApiUsage")
public class ItemBufferRenderer implements BlockEntityRenderer<ItemBufferBlockEntity>
{
    Model model;

    public ItemBufferRenderer(BlockEntityRendererFactory.Context context)
    {
        model = new GlassTankModel(context.getLayerModelPart(NeepMeatClient.MODEL_GLASS_TANK_LAYER));
    }

    @Override
    public void render(ItemBufferBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();

        WritableStackStorage storage = be.getStorage(null);
        ItemStack stack = storage.getResource().toStack((int) storage.getAmount());

        float delta = 0.1f;

        be.stackRenderDelta = MathHelper.lerp(delta, be.stackRenderDelta, storage.getAmount() <= 0 ? 0.3f : 0f);
        matrices.translate(0.5, 0.25f + be.stackRenderDelta, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((be.getWorld().getTime() + tickDelta) * 1));
//
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers, 0);

        matrices.pop();
    }
}