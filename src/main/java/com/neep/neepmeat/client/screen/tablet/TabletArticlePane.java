package com.neep.neepmeat.client.screen.tablet;

import com.neep.neepmeat.guide.article.Article;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

@Environment(value= EnvType.CLIENT)
public class TabletArticlePane extends ContentPane implements Drawable, Element, Selectable
{
    private int page;
    private ArticleTextWidget articleWidget;

    public TabletArticlePane(ITabletScreen parent, Article article)
    {
        super(Text.of(""), parent);
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        articleWidget = new ArticleTextWidget(textRenderer, article);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        if (parent.getAnimationTicks() < 14) return;
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
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        return articleWidget.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    @Override
    public void init()
    {
        super.init();
//        text = new TextFieldWidget(textRenderer, x, y, width, height, Text.of("uwu"));
        addDrawableChild(articleWidget);
        articleWidget.setDimensions(x, y, width, height);
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