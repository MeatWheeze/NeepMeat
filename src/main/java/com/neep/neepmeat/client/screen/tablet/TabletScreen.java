package com.neep.neepmeat.client.screen.tablet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Environment(value= EnvType.CLIENT)
public abstract class TabletScreen extends HandledScreen<ScreenHandler>
{
    public static final Identifier TABLET_TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/tablet/tablet_background.png");

    public List<TabletScreenFactory> SCREENS = new ArrayList<>();

    private float mouseX;
    private float mouseY;
    protected int screenOffsetX = 17;
    protected int screenOffsetY = 17;
    protected int backgroundWidth = 255;
    protected int backgroundHeight = 194;
    protected PlayerEntity player;
    protected int tabWidth = 21;

    public TabletScreen(PlayerEntity player, ScreenHandler handler)
    {
        super(handler, player.getInventory(), new TranslatableText(""));
        this.player = player;
//        SCREENS.add(TabletTextScreen.getFactory(player));
//        SCREENS.add(TabletInventoryScreen.getFactory(player));
        SCREENS.add(TabletMenuScreen.getFactory(player));
    }

    public static TabletScreenFactory getFactory(PlayerEntity player)
    {
        return null;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void switchScreen(TabletScreenFactory factory)
    {
        player.currentScreenHandler = factory.getHandler();
        MinecraftClient.getInstance().setScreen(factory.getScreen(player));
    }

    @Override
    protected void init()
    {
        super.init();
        this.x = (this.width - backgroundWidth) / 2;
        this.y = (this.height - backgroundHeight) / 2;
        for (int i = 0; i < SCREENS.size(); ++i)
        {
            Identifier widget = SCREENS.get(i).getIcon();
            int finalI = i;
            this.addDrawableChild(new TexturedButtonWidget(this.x + 58 + i * 17, this.y + 182, 12, 12, 0, 0, 0, widget, 12, 12, button ->
            {
                this.switchScreen(SCREENS.get(finalI));
            }));
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TABLET_TEXTURE);
//        DrawableHelper.drawTexture(matrices, i, j, 0, 0, 0, backgroundWidth, backgroundHeight, s56, 384);
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void renderTooltip(MatrixStack matrices, ItemStack stack, int x, int y)
    {
        this.renderTooltip(matrices, this.getTooltipFromItem(stack), stack.getTooltipData(), x, y);
    }

    @Override
    public void renderTooltip(MatrixStack matrices, List<Text> lines, Optional<TooltipData> data2, int x, int y)
    {
        List<TooltipComponent> list = lines.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
        data2.ifPresent(data -> list.add(1, TooltipComponent.of(data)));
        this.renderTooltipFromComponents(matrices, list, x, y);
    }

    private void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y)
    {
        TooltipComponent tooltipComponent2;
        int s;
        int k;
        if (components.isEmpty()) {
            return;
        }
        int i = 0;
        int j = components.size() == 1 ? -2 : 0;
        for (TooltipComponent tooltipComponent : components) {
            k = tooltipComponent.getWidth(this.textRenderer);
            if (k > i) {
                i = k;
            }
            j += tooltipComponent.getHeight();
        }
        int l = x + 12;
        int startY = y - 12;
        k = i;
        int m = j;
        if (l + i > this.width) {
            l -= 28 + i;
        }
        if (startY + m + 6 > this.height) {
            startY = this.height - m - 6;
        }
        matrices.push();
        int n = -267386864;
        int o = 0x505000FF;
        int p = 1344798847;
        int q = 400;
        float f = this.itemRenderer.zOffset;
        this.itemRenderer.zOffset = 400.0f;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        int borderCol = -0x50FF50FF;
        // Border top
//        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, startY - 4, l + k + 3, startY - 3, 400, -267386864, -267386864);
        // Border bottom
//        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, startY + m + 3, l + k + 3, startY + m + 4, 400, -267386864, -267386864);
        // Background
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, startY - 3, l + k + 3, startY + m + 3, 400, -267486464, -267386864);
        // Border left
//        Screen.fillGradient(matrix4f, bufferBuilder, l - 4, startY - 3, l - 3, startY + m + 3, 400, -267386864, -267386864);
        // Border right
//        Screen.fillGradient(matrix4f, bufferBuilder, l + k + 3, startY - 3, l + k + 4, startY + m + 3, 400, -267386864, -267386864);
        // Gradient left
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, startY - 3 + 1, l - 3 + 1, startY + m + 3 - 1, 400, borderCol, borderCol);
        // Gradient right
        Screen.fillGradient(matrix4f, bufferBuilder, l + k + 2, startY - 3 + 1, l + k + 3, startY + m + 3 - 1, 400, borderCol, borderCol);
        // Gradient top
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, startY - 3, l + k + 3, startY - 3 + 1, 400, borderCol, borderCol);
        // Gradient bottom
        Screen.fillGradient(matrix4f, bufferBuilder, l - 3, startY + m + 2, l + k + 3, startY + m + 3, 400, borderCol, borderCol);

        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);
        int r = startY;
        for (s = 0; s < components.size(); ++s) {
            tooltipComponent2 = components.get(s);
            tooltipComponent2.drawText(this.textRenderer, l, r, matrix4f, immediate);
            r += tooltipComponent2.getHeight() + (s == 0 ? 2 : 0);
        }
        immediate.draw();
        matrices.pop();
        r = startY;
        for (s = 0; s < components.size(); ++s) {
            tooltipComponent2 = components.get(s);
            tooltipComponent2.drawItems(this.textRenderer, l, r, matrices, this.itemRenderer, 400);
            r += tooltipComponent2.getHeight() + (s == 0 ? 2 : 0);
        }
        this.itemRenderer.zOffset = f;
    }

    public static class TabletScreenFactory
    {
        private final Identifier icon;
        private final Supplier<ScreenHandler> handler;
        private final Supplier<TabletScreen> screenSupplier;

        public TabletScreenFactory(Identifier widget, Supplier<TabletScreen> screenSupplier, Supplier<ScreenHandler> handler)
        {
            this.icon = widget;
            this.handler = handler;
            this.screenSupplier = screenSupplier;
        }

        public TabletScreen getScreen(PlayerEntity player)
        {
            return screenSupplier.get();
        }

        public ScreenHandler getHandler()
        {
            return handler.get();
        }

        public Identifier getIcon()
        {
            return icon;
        }
    }
}
