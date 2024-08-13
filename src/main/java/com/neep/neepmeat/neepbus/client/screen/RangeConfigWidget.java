package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.NMTextField;
import com.neep.meatlib.client.screen.ParentWidget;
import com.neep.neepmeat.client.screen.util.Background;
import com.neep.neepmeat.neepbus.screen.RangeConfigHandler;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

import java.util.List;

public class RangeConfigWidget extends ParentWidget
{
    // For delaying C2S updates
    private int counter = -1;

    private final RangeConfigHandler handler;

    private final TextField valueField;
    private final TextField minField;
    private final TextField maxField;
    private final TextField intervalField;

    private final List<TextField> textFields;

    public RangeConfigWidget(int x, int y, int w, int h, RangeConfigHandler handler)
    {
        super(x, y, w, h);

        this.handler = handler;

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
    public void init()
    {
        super.init();

        Background background = new Background(x, y, w, h, 6);
        addChild(background);

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

    @Override
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

            handler.receiveParamsC2S.emitter().update(intList);
//            updateC2S.accept(intList);
        }
        catch (NumberFormatException e)
        {
            // Don't need to do anything.
            return;
        }
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

    public void setW(int w)
    {
        this.w = w;
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
