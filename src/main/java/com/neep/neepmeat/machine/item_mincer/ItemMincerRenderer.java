package com.neep.neepmeat.machine.item_mincer;

import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Random;

import static com.neep.neepmeat.machine.grinder.GrinderRenderer.renderItems;

@Environment(value= EnvType.CLIENT)
public class ItemMincerRenderer implements BlockEntityRenderer<ItemMincerBlockEntity>
{
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public ItemMincerRenderer(BlockEntityRendererFactory.Context ctx)
    {
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(ItemMincerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        matrices.translate(0.5, 13 / 16f, 0.5);
        WritableStackStorage input = be.storage.inputStorage;
        ItemStack stack = input.getAsStack();

        int j = stack.isEmpty() ? 187 : Item.getRawId(stack.getItem()) + stack.getDamage();
        this.random.setSeed(j);

        renderItems(stack, matrices, vertexConsumers, itemRenderer, be.getWorld(), random, light);
        matrices.pop();
    }
}
