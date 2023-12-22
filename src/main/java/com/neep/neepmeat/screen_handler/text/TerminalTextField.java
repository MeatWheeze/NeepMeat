package com.neep.neepmeat.screen_handler.text;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class TerminalTextField extends TextFieldWidget
{
    public boolean promptMode;
    public int cursorOffset;
    public TextRenderer textRenderer;
    public String prompt = "> ";
    private String cursor = "_";
    protected int editableColor;
    protected int focusedTicks;

    public TerminalTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text)
    {
        super(textRenderer, x, y, width, height, text);
        this.promptMode = true;
        this.cursorOffset = prompt.length();
        this.textRenderer = textRenderer;
    }

    @Override
    public void setEditableColor(int color)
    {
       this.editableColor = color;
        super.setEditableColor(color);
    }

    @Override
    public void eraseCharacters(int characterOffset)
    {
        if (characterOffset + getText().length() + 1 <= cursorOffset && promptMode)
        {
            return;
        }
        super.eraseCharacters(characterOffset);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        int j = this.getCursor();
        int yPos = 0;
        int n = 0;
        for (String string : this.getText().split("\n"))
        {
            n = this.textRenderer.drawWithShadow(matrices, string, this.x, this.y + yPos * textRenderer.fontHeight, editableColor);
            ++yPos;
        }

        boolean bl2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0;
        if (bl2)
        {
            this.textRenderer.drawWithShadow(matrices, cursor, (float) n - (getText().length() - j) * 5, (float) this.y + (yPos - 1) * textRenderer.fontHeight, editableColor);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == GLFW.GLFW_KEY_ENTER)
        {
            setText(getText() + "\n");
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers)
    {
        if (!this.isActive())
        {
            return false;
        }
        super.charTyped(chr, modifiers);
        return false;
    }

    @Override
    public void moveCursor(int offset)
    {
        // Prevent prompt from being deleted in prompt mode.
        if (getCursor() + offset < cursorOffset && promptMode)
        {
            return;
        }
        super.moveCursor(offset);
    }

    @Override
    public void setCursorToStart()
    {
        this.setCursor(cursorOffset);
    }

    @Override
    public void tick()
    {
        ++this.focusedTicks;
        if (promptMode)
        {
            String[] lines = this.getText().split("\n");
            if (!this.getLastLine().startsWith(String.valueOf(prompt)))
            {
//                this.setText(this.getText().replaceAll("\n(?!.*\n)", "\n" + prompt));
                lines[0] = "GLOO";
//                this.setText(String.co);
            }
        }
        super.tick();
    }

    public String getLastLine()
    {
        String[] lines = this.getText().split("\n");
        return lines.length != 0 ? lines[lines.length - 1] : "";
    }
}
