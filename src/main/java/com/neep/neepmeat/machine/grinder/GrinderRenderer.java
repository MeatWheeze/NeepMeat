package com.neep.neepmeat.machine.grinder;

import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

import java.util.Random;

@Environment(value= EnvType.CLIENT)
public class GrinderRenderer implements BlockEntityRenderer<GrinderBlockEntity>
{
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public GrinderRenderer(BlockEntityRendererFactory.Context ctx)
    {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(GrinderBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        matrices.translate(0.5, 13 / 16f, 0.5);
        WritableStackStorage input = be.getStorage().getInputStorage();
        ItemStack stack = input.getAsStack();

        int j = stack.isEmpty() ? 187 : Item.getRawId(stack.getItem()) + stack.getDamage();
        this.random.setSeed(j);

        renderItems(stack, matrices, vertexConsumers, itemRenderer, be.getWorld(), random, light);
    }

    public static void renderItems(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, ItemRenderer itemRenderer, World world, Random random, int light)
    {
        int k = getRenderedAmount(stack);
        BakedModel bakedModel = itemRenderer.getModel(stack, world, null, 0);
        boolean depth = bakedModel.hasDepth();
        float sX = bakedModel.getTransformation().ground.scale.x;
        float sY = bakedModel.getTransformation().ground.scale.y;
        float sZ = bakedModel.getTransformation().ground.scale.z;

        // Rotate by 1 degree to prevent axis fighting with nearby block models
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(1));

        // 2D items lie on their side
        if (!depth)
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));

        float t;
        float s;
        for (int u = 0; u < k; ++u)
        {
            matrices.push();
            if (u > 0)
            {
                if (depth)
                {
                    s = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    t = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float v = (random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    matrices.translate(s, t, v);
                } else
                {
                    s = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    t = (random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    matrices.translate(s, t, 0.0);
                }
            }
            itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, false, matrices, vertices, light, OverlayTexture.DEFAULT_UV, bakedModel);
            matrices.pop();
            if (depth) continue;
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
