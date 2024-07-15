package com.neep.meatweapons.client.renderer.meatgun;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import dev.monarkhes.myron_neepmeat.impl.client.model.MyronBakedModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;

@Environment(EnvType.CLIENT)
public interface MeatgunModuleRenderer<T extends MeatgunModule>
{
    ItemColors COLOURS = new ItemColors();

    void render(ItemStack stack, MeatgunComponent component, T module, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, long time, float tickDelta, int light, int overlay);

    default void renderItem(
            ItemStack stack,
            ModelTransformationMode renderMode,
            boolean leftHanded,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            BakedModel model
    )
    {
        if (!stack.isEmpty())
        {
            matrices.push();

            if (model instanceof MyronBakedModel)
            {
                matrices.translate(0.5, 0, 0.5);
            }

            boolean bl2 = false;

            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);

            renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);

            matrices.pop();
        }
    }

    static void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer consumer)
    {
        Random random = Random.create();
        long l = 42L;

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderBakedItemQuads(matrices, consumer, model.getQuads(null, direction, random), stack, light, overlay);
        }

        random.setSeed(42L);
        renderBakedItemQuads(matrices, consumer, model.getQuads(null, null, random), stack, light, overlay);
    }

    static void renderBakedItemQuads(MatrixStack matrices, VertexConsumer consumer, List<BakedQuad> quads, ItemStack stack, int light, int overlay)
    {
        MatrixStack.Entry entry = matrices.peek();

        for (BakedQuad bakedQuad : quads)
        {
            int i = -1;
            if (bakedQuad.hasColor())
            {
                i = COLOURS.getColor(stack, bakedQuad.getColorIndex());
            }

            float f = (float) (i >> 16 & 0xFF) / 255.0F;
            float g = (float) (i >> 8 & 0xFF) / 255.0F;
            float h = (float) (i & 0xFF) / 255.0F;
            consumer.quad(entry, bakedQuad, f, g, h, light, overlay);
        }
    }

    @FunctionalInterface
    interface Factory<T extends MeatgunModule>
    {
        MeatgunModuleRenderer<T> create(MinecraftClient client);
    }
}
