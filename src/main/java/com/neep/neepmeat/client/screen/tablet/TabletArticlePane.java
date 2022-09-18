package com.neep.neepmeat.client.screen.tablet;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(value= EnvType.CLIENT)
public class TabletArticlePane extends ContentPane implements Drawable, Element, Selectable
{
    private int page;
    private ArticleTextWidget article;

    public TabletArticlePane(ITabletScreen parent, Text text)
    {
        super(Text.of("eeeee"), parent);
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        article = new ArticleTextWidget(textRenderer, text);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
        GUIUtil.renderBorder(matrices, x, y, width, height, 0xFF888800, 0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode != GLFW.GLFW_KEY_ESCAPE)
        {
            return false;
        }
        super.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        if (!(chr == GLFW.GLFW_KEY_ESCAPE))
        {
        }
        super.charTyped(chr, modifiers);
        return true;
    }

    @Override
    public void init()
    {
        super.init();
//        text = new TextFieldWidget(textRenderer, x, y, width, height, Text.of("uwu"));
        addDrawableChild(article);
        article.setDimensions(x, y, width, height);
    }

    protected int getPageLines()
    {
        return 30;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder)
    {

    }

    @Override
    public SelectionType getType()
    {
        return SelectionType.FOCUSED;
    }
}