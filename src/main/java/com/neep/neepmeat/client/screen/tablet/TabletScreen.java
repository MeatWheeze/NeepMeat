package com.neep.neepmeat.client.screen.tablet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.guide.GuideNode;
import com.neep.neepmeat.guide.article.Article;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
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

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Environment(value= EnvType.CLIENT)
public class TabletScreen extends HandledScreen<ScreenHandler> implements ITabletScreen
{
    public static final Identifier TABLET_TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/tablet/tablet_background.png");

    public List<TabletScreenFactory> SCREENS = new ArrayList<>();

    private float mouseX;
    private float mouseY;
    protected int screenOffsetX = 17;
    protected int screenOffsetY = 17;
    protected int backgroundWidth = 255;
    protected int backgroundHeight = 194;
    protected int contentWidth = 340;
    protected int contentHeight = 280;
    protected int screenWidth = 156;
    protected int screenHeight = 145;
    protected PlayerEntity player;
    protected int tabWidth = 21;

    protected ContentPane leftPane;
    protected ContentPane rightPane;

    // Current location within the entry tree
    protected final Deque<GuideNode> path = new LinkedList<>();

    public TabletScreen(PlayerEntity player, ScreenHandler handler)
    {
        super(handler, player.getInventory(), new TranslatableText(""));
        this.player = player;
        this.leftPane = new TabletListPane(player, this);
        this.rightPane = new TabletArticlePane(this, Article.EMPTY);
    }

    @Override
    public void setLeftPane(ContentPane element)
    {
        remove(leftPane);
        this.leftPane = element;
        init();
    }

    @Override
    public void setRightPane(ContentPane element)
    {
        remove(rightPane);
        this.rightPane = element;
        init();
    }

    @Override
    public void push(GuideNode node)
    {
        path.push(node);
    }

    @Override
    public Deque<GuideNode> getPath()
    {
        return path;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        int borderCol = 0xFF008800;
        int offset = 3;
        GUIUtil.renderBorder(matrices, x, y, contentWidth, contentHeight, borderCol, offset);
    }


    protected void enterNode(GuideNode node)
    {
        path.push(node);
        node.visitScreen(this);
        init();
    }

    @Override
    protected void init()
    {

        super.init();
        addDrawableChild(leftPane);
        addDrawableChild(rightPane);
        contentWidth = (int) (this.width * 0.7);
        contentHeight = (int) (this.height * 0.7);
        this.x = (this.width - contentWidth) / 2;
        this.y = (this.height - contentHeight) / 2;

        float ratio = 0.4f;
        int leftWidth = (int) (ratio * contentWidth);
        int rightWidth = (int) ((1 - ratio) * contentWidth);
        int rightStart = this.x + leftWidth;

        if (leftPane != null)
        {
            leftPane.setDimensions(x, y, leftWidth, 120);
            leftPane.init(client);
        }
        if (rightPane != null)
        {
            rightPane.setDimensions(rightStart, y, rightWidth, contentHeight);
            rightPane.init(client);
        }
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TABLET_TEXTURE);
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
