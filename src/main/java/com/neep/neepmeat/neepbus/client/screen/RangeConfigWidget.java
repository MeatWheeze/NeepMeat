package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.NMTextField;
import com.neep.neepmeat.client.screen.util.Background;
import com.neep.neepmeat.client.screen.util.ClickableWidget;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
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
import java.util.function.Consumer;

public class RangeConfigWidget extends AbstractParentElement implements ClickableWidget, Selectable
{
    // For delaying C2S updates
    private int counter;

    protected int x, y;
    protected int w;
    protected int h;
    private final Consumer<List<Integer>> updateC2S;

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = client.textRenderer;

    private final List<ClickableWidget> positionables = new ObjectArrayList<>();
    private final List<Drawable> drawables = new ObjectArrayList<>();

    private final TextField valueField;
    private final TextField minField;
    private final TextField maxField;
    private final TextField intervalField;

    private final List<TextField> textFields;

    public RangeConfigWidget(int x, int y, int w, int h, Consumer<List<Integer>> updateC2S)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.updateC2S = updateC2S;

        valueField = new TextField(textRenderer, x, y, w, 8, Text.of("Value: "));
        minField = new TextField(textRenderer, x, y, w, 8, Text.of("Min: "));
        maxField = new TextField(textRenderer, x, y, w, 8, Text.of("Max: "));
        intervalField = new TextField(textRenderer, x, y, w, 8, Text.of("Interval: "));

        textFields = List.of(valueField, minField, maxField, intervalField);
    }

    void updateParams(List<Integer> integers)
    {
        if (integers.size() != textFields.size())
        {
            NeepMeat.LOGGER.error("RangeConfigWidget: Incorrect number of integers received");
            return; // Something has gone horribly wrong and I don't want to think about it.
        }

        for (int i = 0; i < integers.size(); ++i)
        {
            textFields.get(i).setIntText(integers.get(i));
        }
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
        field = addChild(intervalField);
        field.setPos(x, yOff);
        field.drawFancyBackground(false);
        yOff += field.h() + 1;

        h = yOff - y;
        background.setH(h);
    }

    public void tick()
    {
        if (counter != -1)
        {
            counter = Math.min(counter + 1, 10);

            if (counter == 10)
            {
                update();
                counter = -1;
            }
        }
    }

    // Parses all fields and sends them to the server.
    protected void update()
    {
        try
        {
            IntList intList = new IntArrayList(textFields.size());
            for (var field : textFields)
            {
                String text = field.getText();
                int parsed = !text.isEmpty() && text.matches("[0-9]*") ? Integer.parseInt(text) : 0;
                intList.add(parsed);
            }

            updateC2S.accept(intList);
        }
        catch (NumberFormatException e)
        {
            // Don't need to do anything.
            return;
        }
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

    private class TextField extends NMTextField
    {
        private final Text prefix;

        private boolean ignoreChange;

        public TextField(TextRenderer textRenderer, int x, int y, int width, int height, Text prefix)
        {
            super(textRenderer, x, y, width, height, Text.empty());
            setChangedListener(this::onChanged);
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
            ignoreChange = true;
            setText(Integer.toString(value));
            ignoreChange = false;
        }

        // Can't be bothered to add another AW entry and restart the IDE and generate sources again.
        private void onChanged(String s)
        {
            if (!ignoreChange)
                counter = 0;
        }
    }
}
