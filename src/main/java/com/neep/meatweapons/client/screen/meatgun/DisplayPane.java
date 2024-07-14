package com.neep.meatweapons.client.screen.meatgun;

import com.neep.meatweapons.MWItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

class DisplayPane extends TinkerTableScreen.PaneWidget
{
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    private final Slot slot;

    private float offsetX = 0;
    private float offsetY = 0;
    private float rotY = MathHelper.HALF_PI;
    private float rotX = 0;
    private float scale = 5;

    private ItemStack stack = ItemStack.EMPTY;

    public DisplayPane(Slot slot)
    {
        this.slot = slot;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

        float cx = bounds.x() + bounds.w() / 2f + offsetX;
        float cy = bounds.y() + bounds.h() / 2f + offsetY;

        int zOffset = stack.isOf(MWItems.MEATGUN_PISTOL) ? 4 : 0;
        Matrix4f modelTransform = new Matrix4f()
                .translate(0, 8, zOffset)
                .scale(scale)
                .rotateX(rotX)
                .rotateY(rotY)
                .translate(0, -8, -zOffset)
                ;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(cx, cy, 150);
        matrices.multiplyPositionMatrix(modelTransform);
        matrices.multiplyPositionMatrix((new Matrix4f()).scaling(1.0F, -1.0F, 1.0F));
        matrices.scale(16.0F, 16.0F, 16.0F);
        context.enableScissor(bounds.x() + 2, bounds.y() + 2, bounds.x() + bounds.w() - 2, bounds.y() + bounds.h() - 2);
        itemRenderer.renderItem(stack, ModelTransformationMode.NONE,
                15728880, OverlayTexture.DEFAULT_UV,
                context.getMatrices(), context.getVertexConsumers(), null, 0);
        context.draw(); // Draw now so that the scissor works consistently (?)
        context.disableScissor();
        matrices.pop();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1)
        {
            offsetX += deltaX;
            offsetY += deltaY;
        }
        else
        {
            rotY += deltaX / 40;
            rotX -= deltaY / 40;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        scale += amount / 10;
        return true;
    }

    public void tick()
    {
        stack = slot.getStack();
//        meatgun = MWComponents.MEATGUN.getNullable(slot.getStack());
    }
}
