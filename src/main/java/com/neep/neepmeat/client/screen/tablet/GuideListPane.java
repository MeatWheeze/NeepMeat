package com.neep.neepmeat.client.screen.tablet;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.guide.GuideNode;
import com.neep.neepmeat.guide.GuideReloadListener;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Environment(value= EnvType.CLIENT)
public class GuideListPane extends ContentPane implements Drawable, Element, Selectable
{
    // Currently available entries
    protected final List<EntryWidget> entries = new ArrayList<>();

    private GuideNode lastNode;
    private int selected;
    protected int entryHeight = 11;
    protected int contentHeight;

    private int animationTicks;

    private boolean searchMode;
    private final StringBuilder searchString = new StringBuilder();

    public GuideListPane(GuideScreen parent)
    {
        super(Text.of("eeeee"), parent);
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        if (parent.getAnimationTicks() < 12) return;
        super.render(matrices, mouseX, mouseY, delta);
        GUIUtil.renderBorder(matrices, x, y, width, height, 0xFF888800, 0);

        if (selected != -1)
        {
            GUIUtil.renderBorder(matrices, x + screenOffsetX, y + screenOffsetY + selected * entryHeight, width - 2 * screenOffsetX, entryHeight, 0xFFAAAA00, 0);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (searchMode)
        {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE)
            {
                searchString.setLength(0);
                searchMode = false;
                init();
                return true;
            }
            else if (keyCode == GLFW.GLFW_KEY_TAB)
            {
                setSelected(getSelected() + (modifiers == GLFW.GLFW_MOD_SHIFT ? -1 : 1));
//                setSelected(getSelected() + 1);
            }
            else if (keyCode == GLFW.GLFW_KEY_BACKSPACE)
            {
                erase(-1);
            }
        }
        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        if (!(chr == GLFW.GLFW_KEY_ESCAPE))
        {
            if (searchMode)
            {
                write(chr, modifiers);
                return true;
            }

            if (chr == GLFW.GLFW_KEY_SLASH)
            {
                searchMode = true;
                init();
            }
            if (chr == GLFW.GLFW_KEY_J)
            {
                setSelected(getSelected() + 1);
            }
            if (chr == GLFW.GLFW_KEY_K)
            {
                setSelected(getSelected() - 1);
            }
        }
        super.charTyped(chr, modifiers);
        return true;
    }

    protected void write(char chr, int modifiers)
    {
        searchString.append(Character.toLowerCase(chr));
        init();
    }

    protected void erase(int dist)
    {
        if (searchString.length() == 0) return;
        searchString.delete(searchString.length() - 1, searchString.length());
        init();
    }

    @Override
    public void tick()
    {
        ++animationTicks;
        super.tick();
    }

    @Override
    public void init()
    {
        super.init();

        // Subtract the y offset and the height of the search widget
        this.contentHeight = height - screenOffsetY - entryHeight;

        clearChildren();
        if (!searchMode) generateMenu();
        else updateSearch();
        addDrawableChild(new SearchWidget(x, y + height - entryHeight, width, entryHeight, Text.of("")));

        if (lastNode != parent.getPath().peek())
        {
            entryAnimationStart = parent.getAnimationTicks();
            selected = -1;
            lastNode = parent.getPath().peek();
        }
    }

    protected void updateSearch()
    {
        entries.clear();

        // Create a de-duplicated set of matching entries.
        // GuideNode.GuideNodeImpl::equals() only checks the ID string since there is no reason for multiple entries to share an ID but have different contents.
        Iterator<GuideNode> filtered = GuideReloadListener.getInstance().getArticleNodes().stream().distinct().filter(
                a -> a.getText().toString().toLowerCase().contains(searchString)).iterator();

        for (int i = 0; filtered.hasNext() && (i + 1) * entryHeight < contentHeight; ++i)
        {
            GuideNode node = filtered.next();
            ItemStack icon = new ItemStack(Registries.ITEM.get(node.getIcon()));
            entries.add(new EntryWidget(i,
                    screenOffsetX + this.x,
                    screenOffsetY + this.y + (i) * entryHeight,
                    width - 2 * screenOffsetX,
                    entryHeight,
                    icon,
                    node.getText(),
                    node));
        }
        entries.forEach(this::addDrawableChild);
    }

    private int entryAnimationStart;

    protected void generateMenu()
    {
        entries.clear();

        // Back button
        entries.add(new EntryWidget(0, screenOffsetX + this.x, screenOffsetY + this.y, width - 2 * screenOffsetX, entryHeight, ItemStack.EMPTY, Text.of("\u2190"), GuideNode.BACK));

        List<GuideNode> nodes = parent.getPath().peek().getChildren();

        for (int i = 0; i < nodes.size(); ++i)
        {
            GuideNode node = nodes.get(i);
            ItemStack icon = new ItemStack(Registries.ITEM.get(node.getIcon()));
            entries.add(new EntryWidget(i + 1,
                    screenOffsetX + this.x,
                    screenOffsetY + this.y + (i + 1) * entryHeight,
                    width - 2 * screenOffsetX,
                    entryHeight,
                    icon,
                    node.getText(),
                    node));
        }
        entries.forEach(this::addDrawableChild);
    }

//    protected int getPageEntries()
//    {
//        return (int) Math.floor(height / (float) entryHeight);
//    }

    public void setSelected(int sel)
    {
        this.selected = NMMaths.wrap(sel, 0, entries.size() - 1);
        entries.get(this.selected).onPress();
    }

    public int getSelected()
    {
        return selected;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder)
    {

    }

    @Override
    public SelectionType getType()
    {
        return SelectionType.NONE;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public class SearchWidget extends ClickableWidget
    {
        private Text searchMessage = Text.of("Type '/' to search");

        public SearchWidget(int x, int y, int width, int height, Text message)
        {
            super(x, y, width, height, message);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            if (!searchMode)
            {
                textRenderer.draw(matrices, searchMessage, this.x + 2, this.y + (this.height - 7) / 2f, 0x008800);
            }
            else
            {
                textRenderer.draw(matrices, "/" + searchString, this.x + 2, this.y + (this.height - 7) / 2f, 0x008800);
            }
        }
    }

    public class EntryWidget extends ClickableWidget
    {
        private final GuideNode node;
        private final ItemStack icon;
        private final int index;

        public EntryWidget(int index, int x, int y, int w, int h, ItemStack icon, Text text, GuideNode node)
        {
            super(x, y, w, h, text);
            this.index = index;
            this.node = node;
            this.icon = icon;
        }

        @Override
        public void playDownSound(SoundManager soundManager)
        {
            soundManager.play(PositionedSoundInstance.master(NMSounds.MARATHON_BEEP, 1.0f));
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            this.onPress();
        }

        protected void onPress()
        {
            selected = index;
            node.visitScreen(parent);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            // Delay animation by 16 ticks if the screen has been opened for fewer than 32 ticks.
            if (parent.getAnimationTicks() < (animationTicks <= 32 ? 16 : 0) + entryAnimationStart + index) return;

            VertexConsumerProvider vertexConsumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
            this.renderBackground(matrices, MinecraftClient.getInstance(), mouseX, mouseY);
//            int borderCol = getSelected() == index ? 0xFF00CC00 : 0xFF008800;
            int borderCol = 0xFF008800;
            this.drawHorizontalLine(matrices, this.x, this.x + width, this.y + height, borderCol);
            this.drawHorizontalLine(matrices, this.x, this.x + width, this.y, borderCol);
            this.drawVerticalLine(matrices, this.x, this.y, this.y + height, borderCol);
            this.drawVerticalLine(matrices, this.x + width, this.y, this.y + height, borderCol);
//            int j = this.active ? 0xFFFFFF : 0xA0A0A0;
            textRenderer.draw(matrices, this.getMessage(), this.x + 2, this.y + (this.height - 7) / 2f, 0x008800);
            if (node.getChildren().size() > 0)
            {
                textRenderer.draw(matrices, "\u2192", this.x + this.width - 9, this.y + (this.height - 7) / 2f, 0x008800);
            }
            else
            {
                renderItemIcon(this.x + width - 16, this.y - 1, itemRenderer, getZOffset(), icon, matrices, vertexConsumers, 15, 0);
            }
        }
    }

    public static void renderItemIcon(int x, int y, ItemRenderer renderer, int zOffset, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        if (stack.isEmpty())
        {
            return;
        }

        ItemModels models = renderer.getModels();
        BakedModel bakedModel = models.getModel(stack);

        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0f + zOffset);
        matrixStack.translate(8.0, 7.0, 0.0); // Positive y is downwards on the screen
        matrixStack.scale(1.0f, -1.0f, 1.0f);
        matrixStack.scale(10.0f, 10.0f, 10.0f);
        RenderSystem.applyModelViewMatrix();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
//        renderer.renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, bakedModel);
        renderItem(stack, matrices, vertexConsumers, light, overlay, bakedModel);
        immediate.draw();
        RenderSystem.enableDepthTest();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();

//        renderer.zOffset = bakedModel.hasDepth() ? zOffset + 50.0f + 0 : zOffset + 50.0f;
//
//        BakedModel model = renderer.getModels().getModel(stack);
//        renderItem(stack, matrices, vertexConsumers, light, overlay, model);
    }

    public static void renderItem(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model)
    {
        ModelTransformation.Mode renderMode = ModelTransformation.Mode.GUI;
        matrices.push();
        model.getTransformation().getTransformation(renderMode).apply(false, matrices);
        matrices.translate(-0.5, -0.5, -0.5);
        RenderLayer renderLayer = RenderLayers.getItemLayer(stack, true);
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
        renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
        matrices.pop();
    }

    private static void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices)
    {
        var random = Random.create();

        for (Direction direction : Direction.values())
        {
            random.setSeed(42L);
            renderGreenQuads(matrices, vertices, model.getQuads(null, direction, random), stack, light, overlay);
        }
        random.setSeed(42L);
        renderGreenQuads(matrices, vertices, model.getQuads(null, null, random), stack, light, overlay);
    }

    private static void renderGreenQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay)
    {
        MatrixStack.Entry entry = matrices.peek();
        for (BakedQuad bakedQuad : quads)
        {

            int i = 0xFFFFFF;
            float r = (float)(i >> 16 & 0xFF) / 255.0f;
            float g = (float)(i >> 8 & 0xFF) / 255.0f;
            float b = (float)(i & 0xFF) / 255.0f;

            vertices.quad(entry, bakedQuad, r, g, b, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV);
        }
    }
}