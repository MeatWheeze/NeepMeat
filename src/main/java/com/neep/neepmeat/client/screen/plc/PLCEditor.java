package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.plc.edit.EditBoxWidget;
import com.neep.neepmeat.neepasm.NeepASM;
import com.neep.neepmeat.neepasm.compiler.ParsedSource;
import com.neep.neepmeat.neepasm.compiler.Parser;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import com.neep.neepmeat.plc.instruction.Argument;
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

    private final Parser parser = new Parser();

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
            textField = new EditBoxWidget(client.textRenderer, x, y, 300, height, Text.of("clom"), Text.of("gle"))
            {
                @Override
                protected void setFocused(boolean focused)
                {
                    super.setFocused(focused);
                    updateEditorWidth();
                }
            };
            textField.setText(parent.getScreenHandler().getInitialText());
            textField.setChangeListener(this::setChanged);

        }

        updateEditorWidth();

        addDrawableChild(textField);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void setChanged(String s)
    {
        changed = true;
    }

    private void updateEditorWidth()
    {
        if (textField.isFocused())
            textField.setWidth(300);
        else
            textField.setWidth(100);

        textField.setHeight(height);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (client.world.getTime() % 10 == 0 && changed)
        {
            try
            {
                ParsedSource parsedSource = parser.parse(textField.getText());
                textField.setError("Parsed successfully", 0x44AA00);
            }
            catch (NeepASM.ProgramBuildException e)
            {
                textField.setError(e.getMessage(), 0xFF0000);
            }

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

    public void argument(Argument argument)
    {
        textField.insert("@(" + argument.pos().getX() + " " + argument.pos().getY() + " " + argument.pos().getZ() + " " + argument.face().name().toUpperCase().charAt(0) + ")");
    }
}
