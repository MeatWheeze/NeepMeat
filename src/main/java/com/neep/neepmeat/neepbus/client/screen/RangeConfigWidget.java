package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.client.screen.NMTextField;
import com.neep.neepmeat.client.screen.util.Background;
import com.neep.neepmeat.client.screen.util.ClickableWidget;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

import java.util.List;

public class RangeConfigWidget extends AbstractParentElement implements ClickableWidget, Selectable
{
    private int x, y;
    private int w, h;

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = client.textRenderer;

    private final List<ClickableWidget> positionables = new ObjectArrayList<>();
    private final List<Drawable> drawables = new ObjectArrayList<>();

    private final TextField valueField;
    private final TextField minField;
    private final TextField maxField;
    private final TextField divField;

    public RangeConfigWidget(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        valueField = new TextField(textRenderer, x, y, w, 8, Text.of("Value: "));
        minField = new TextField(textRenderer, x, y, w, 8, Text.of("Min: "));
        maxField = new TextField(textRenderer, x, y, w, 8, Text.of("Max: "));
        divField = new TextField(textRenderer, x, y, w, 8, Text.of("Divisions: "));
    }

    void updateParams(int value, int min, int max, int divisions)
    {
        valueField.setIntText(value);
        minField.setIntText(min);
        maxField.setIntText(max);
        divField.setIntText(divisions);
    }

    @Override
    public List<ClickableWidget> children()
    {
        return positionables;
    }

    protected <T extends ClickableWidget> T addChild(T widget)
    {
        positionables.add(widget);
        return widget;
    }

    public void init()
    {
        drawables.clear();
        children().clear();

        Background background = new Background(x, y, w, h, 6);
        drawables.add(background);

        int yOff = y;

        TextField field;
        field = addChild(valueField);
        field.setPos(x, yOff);
        field.drawFancyBackground(false);
        yOff += field.h() + 1;
        field = addChild(minField);
        field.setPos(x, yOff);
        field.drawFancyBackground(false);
        yOff += field.h() + 1;
        field = addChild(maxField);
        field.setPos(x, yOff);
        field.drawFancyBackground(false);
        yOff += field.h() + 1;
        field = addChild(divField);
        field.setPos(x, yOff);
        field.drawFancyBackground(false);
        yOff += field.h() + 1;

        h = yOff - y;
        background.setH(h);
    }

    @Override
    public int x() { return x; }

    @Override
    public int y() { return y; }

    @Override
    public int w() { return w; }
    @Override
    public int h() { return h; }

    @Override
    public void setPos(int x, int y)
    {
        this.x = x;
        this.y = y;
        init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        drawables.forEach(drawable -> drawable.render(context, mouseX, mouseY, delta));
        children().forEach(clickableWidget -> clickableWidget.render(context, mouseX, mouseY, delta));
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

    private static class TextField extends NMTextField
    {
        private final Text prefix;

        public TextField(TextRenderer textRenderer, int x, int y, int width, int height, Text prefix)
        {
            super(textRenderer, x, y, width, height, Text.empty());
            this.prefix = prefix;
        }

        @Override
        public String getPrefix()
        {
            return prefix.getString();
        }

        @Override
        protected int getTextStart()
        {
            return 48;
        }

        @Override
        public void write(String text)
        {
            if (!text.matches("[0-9]*"))
                return;

            super.write(text);
        }

        public void setIntText(int value)
        {
            setText(Integer.toString(value));
        }
    }
}
