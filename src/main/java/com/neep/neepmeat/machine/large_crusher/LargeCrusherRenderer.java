package com.neep.neepmeat.machine.large_crusher;

import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.machine.grinder.GrinderBlock;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class LargeCrusherRenderer implements BlockEntityRenderer<LargeCrusherBlockEntity>
{
    private final ItemRenderer itemRenderer;
    private final Random random = new java.util.Random();

    public LargeCrusherRenderer(BlockEntityRendererFactory.Context ctx)
    {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(LargeCrusherBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        if (!be.getCachedState().get(LargeCrusherBlock.ASSEMBLED))
            return;

        List<LargeCrusherStorage.InputSlot> slots = be.getSlots();

        double sinTime = NMMaths.sin(be.getWorld().getTime(), tickDelta, 1);

        Direction facing = be.getCachedState().get(GrinderBlock.FACING);

        matrices.push();
        BERenderUtils.rotateFacing(facing, matrices);

        float[] offsets = {-0.25f, 0.25f, -0.75f, 0.75f};

        float yOffset = 2 + 0 / 16f;
        if (be.progressIncrement() > 0)
        {
            yOffset += Math.abs(sinTime * 0.02);
        }

        matrices.push();
        matrices.translate(0.5, yOffset, 0.5);
        for (int i = 0; i < slots.size(); ++i)
        {
            LargeCrusherStorage.InputSlot input = slots.get(i);
            ItemStack stack = input.getAsStack();

            matrices.push();

            matrices.translate(offsets[i], 0, -1.3);

            int j = stack.isEmpty() ? 187 : Item.getRawId(stack.getItem()) + stack.getDamage();
            this.random.setSeed(j);

            matrices.translate(
                    ((random.nextFloat() * 2) - 1) * 0.15,
                    ((random.nextFloat() * 2) - 1) * 0.15,
                    ((random.nextFloat() * 2) - 1) * 0.15);
            matrices.scale(2.4f, 2.4f, 2.4f);
            renderItems(stack, matrices, vertexConsumers, itemRenderer, be.getWorld(), random, light);

            matrices.pop();
        }
        matrices.pop();
        matrices.pop();
    }

    public static void renderItems(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, ItemRenderer itemRenderer, World world, Random random, int light)
    {
        matrices.push();
        int k = getRenderedAmount(stack);
        BakedModel bakedModel = itemRenderer.getModel(stack, world, null, 0);
        boolean depth = bakedModel.hasDepth();
        float sX = bakedModel.getTransformation().ground.scale.x;
        float sY = bakedModel.getTransformation().ground.scale.y;
        float sZ = bakedModel.getTransformation().ground.scale.z;

        // Rotate by 1 degree to prevent axis fighting with nearby block models
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(1));


        float t;
        float s;
        float v;
        for (int u = 0; u < k; ++u)
        {
            matrices.push();
            if (u > 0)
            {
                if (depth)
                {
                    s = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    t = (random.nextFloat() * 2.0f) * 0.15f;
                    v = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    matrices.translate(s, t, v);
                } else
                {
                    s = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    t = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    matrices.translate(s, t, 0.0);
                }
            }
            matrices.translate(0, 0, 0.25);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(depth ? 0 : -20));
            matrices.translate(0, 0, -0.25);
            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, false, matrices, vertices, light, OverlayTexture.DEFAULT_UV, bakedModel);
            matrices.pop();
            if (depth)
                continue;
            matrices.translate(0.0f * sX, 0.0f * sY, 0.09375f * sZ);
        }
        matrices.pop();
    }

    public static int getRenderedAmount(ItemStack stack)
    {
        int i = 1;
        if (stack.getCount() > 48)
        {
            i = 5;
        }
        else if (stack.getCount() > 32)
        {
            i = 4;
        }
        else if (stack.getCount() > 16)
        {
            i = 3;
        }
        else if (stack.getCount() > 1)
        {
            i = 2;
        }
        return i;
    }
}
