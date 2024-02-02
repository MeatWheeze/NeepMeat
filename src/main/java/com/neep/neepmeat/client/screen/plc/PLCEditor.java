package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.plc.edit.EditBoxWidget;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;

public class PLCEditor extends ScreenSubElement implements Drawable, Element, Selectable
{
    private final PLCProgramScreen parent;
    private EditBoxWidget textField;
    private boolean changed;

    public PLCEditor(PLCProgramScreen parent)
    {
        this.parent = parent;
    }

    @Override
    protected void init()
    {
        super.init();
        if (textField == null)
        {
            textField = new EditBoxWidget(client.textRenderer, x, y, 100, height, Text.of("clom"), Text.of("gle"));
            textField.setText(parent.getScreenHandler().getInitialText());
            textField.setChangeListener(this::setChanged);

        }

        textField.setWidth(100);

        addDrawableChild(textField);
    }

    private void setChanged(String s)
    {
        changed = true;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (client.world.getTime() % 10 == 0 && changed)
        {
            PLCSyncProgram.Client.sendText(parent.getScreenHandler().getPlc(), textField.getText());
            changed = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        try
        {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        try
        {
            return super.charTyped(chr, modifiers);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
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

    public boolean isTextSelected()
    {
        return textField.isFocused();
    }
}
