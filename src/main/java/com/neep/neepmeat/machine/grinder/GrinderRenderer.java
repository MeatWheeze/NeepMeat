package com.neep.neepmeat.machine.grinder;

import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.util.Random;

@Environment(value= EnvType.CLIENT)
public class GrinderRenderer implements BlockEntityRenderer<GrinderBlockEntity>
{
    private final Random random = new Random();
    private final ItemRenderer itemRenderer;

    public GrinderRenderer(BlockEntityRendererFactory.Context ctx)
    {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(GrinderBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        double sinTime = Math.sin(be.getWorld().getTime()) * Math.cos(tickDelta) + Math.cos(be.getWorld().getTime()) * Math.sin(tickDelta);

        Direction facing = be.getCachedState().get(GrinderBlock.FACING);

        matrices.push();
        BERenderUtils.rotateFacing(facing, matrices);
        float yOffset = 11 / 16f;
        if (be.currentRecipe != null && be.progressIncrement() > 0)
        {
            yOffset += Math.abs(sinTime * 0.02);
        }

        matrices.push();
        matrices.translate(0.5, yOffset, 0.5);
        WritableStackStorage input = be.getStorage().getInputStorage();
        ItemStack stack = input.getAsStack();

        int j = stack.isEmpty() ? 187 : Item.getRawId(stack.getItem()) + stack.getDamage();
        this.random.setSeed(j);

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-45));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        renderItems(stack, matrices, vertexConsumers, itemRenderer, be.getWorld(), random, light);
        matrices.pop();
    }

    public static void renderItems(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, ItemRenderer itemRenderer, World world, Random random, int light)
    {
        int k = getRenderedAmount(stack);
        BakedModel bakedModel = itemRenderer.getModel(stack, world, null, 0);
        boolean depth = bakedModel.hasDepth();
        float sX = bakedModel.getTransformation().ground.scale.getX();
        float sY = bakedModel.getTransformation().ground.scale.getY();
        float sZ = bakedModel.getTransformation().ground.scale.getZ();

        // Rotate by 1 degree to prevent axis fighting with nearby block models
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(1));


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
            itemRenderer.renderItem(stack, ModelTransformation.Mode.GROUND, false, matrices, vertices, light, OverlayTexture.DEFAULT_UV, bakedModel);
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
