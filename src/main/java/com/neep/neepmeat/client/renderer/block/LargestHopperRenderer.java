package com.neep.neepmeat.client.renderer.block;

import com.neep.neepmeat.machine.live_machine.block.entity.LargestHopperBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

import static com.neep.neepmeat.machine.large_crusher.LargeCrusherRenderer.renderItems;

public class LargestHopperRenderer implements BlockEntityRenderer<LargestHopperBlockEntity>
{
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public LargestHopperRenderer(BlockEntityRendererFactory.Context context)
    {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(LargestHopperBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        float[] xOffsets = {-0.45f, 0.45f, 0.45f, -0.45f};
        float[] zOffsets = {-0.45f, 0.45f, -0.45f, 0.45f};

        float yOffset = 0.25f;
        matrices.push();
        matrices.translate(0.5, yOffset, 0.5);
        var slots = be.getInventory().getItems();
        for (int i = 0; i < slots.size(); ++i)
        {
            ItemStack stack = slots.get(i);

            matrices.push();

            matrices.translate(xOffsets[i], 0, zOffsets[i]);

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
    }
}
