package com.neep.neepmeat.machine.grinder;

import com.neep.neepmeat.storage.WritableStackStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

import java.util.Random;

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

        int k = this.getRenderedAmount(stack);
        BakedModel bakedModel = this.itemRenderer.getModel(stack, be.getWorld(), null, 0);
        boolean depth = bakedModel.hasDepth();
        float sX = bakedModel.getTransformation().ground.scale.getX();
        float sY = bakedModel.getTransformation().ground.scale.getY();
        float sZ = bakedModel.getTransformation().ground.scale.getZ();
        if (!depth)
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));

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
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float v = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    matrices.translate(s, t, v);
                } else
                {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    matrices.translate(s, t, 0.0);
                }
            }
            this.itemRenderer.renderItem(stack, ModelTransformation.Mode.GROUND, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, bakedModel);
            matrices.pop();
            if (depth) continue;
            matrices.translate(0.0f * sX, 0.0f * sY, 0.09375f * sZ);
        }
        matrices.pop();
    }

    private int getRenderedAmount(ItemStack stack)
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
