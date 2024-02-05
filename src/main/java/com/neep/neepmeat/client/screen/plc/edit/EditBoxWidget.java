package com.neep.neepmeat.client.screen.plc.edit;

import com.mojang.blaze3d.platform.GlStateManager.LogicOp;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class EditBoxWidget extends ScrollableWidget
{
    private final TextRenderer textRenderer;
    private final EditBox editBox;
    private int tick;

    private String errorMessage;
    private int errorCol = 0xFFFFFFFF;

    private float scale = 0.8f;
    private int errorLine = -1;
    private int debugLine = -1;

    public EditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text message)
    {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.editBox = new EditBox(textRenderer, width - this.getPaddingDoubled(), scale);
        this.editBox.setCursorChangeListener(this::onCursorChange);
    }

    public void setMaxLength(int maxLength)
    {
        this.editBox.setMaxLength(maxLength);
    }

    public void setChangeListener(Consumer<String> changeListener)
    {
        this.editBox.setChangeListener(changeListener);
    }

    public String getText()
    {
        return this.editBox.getText();
    }

    public void insert(String s)
    {
        EditBox.Substring selection = editBox.getSelection();
        if (!editBox.getText().isEmpty() && !Character.isWhitespace(editBox.getText().charAt(selection.beginIndex() - 1)))
        {
            s = " " + s;
        }
        editBox.replaceSelection(s);
    }

    public void setText(String text)
    {
        this.editBox.setText(text);
    }

    public void tick()
    {
        ++this.tick;
    }

    public void appendNarrations(NarrationMessageBuilder builder)
    {
        builder.put(NarrationPart.TITLE, Text.translatable("narration.edit_box", this.getText()));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (super.mouseClicked(mouseX, mouseY, button))
        {
            return true;
        }
        else if (this.isWithinBounds(mouseX, mouseY) && button == 0)
        {
            this.editBox.setSelecting(Screen.hasShiftDown());
            this.moveCursor(mouseX, mouseY);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
    {
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY))
        {
            return true;
        }
        else if (this.isWithinBounds(mouseX, mouseY) && button == 0)
        {
            this.editBox.setSelecting(true);
            this.moveCursor(mouseX, mouseY);
            this.editBox.setSelecting(Screen.hasShiftDown());
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        return this.editBox.handleSpecialKey(keyCode);
    }

    public boolean charTyped(char chr, int modifiers)
    {
        if (this.visible && this.isFocused() && SharedConstants.isValidChar(chr))
        {
            this.editBox.replaceSelection(Character.toString(chr));
            return true;
        }
        else
        {
            return false;
        }
    }

    private int textCol()
    {
        return PLCCols.TEXT.col;
    }

    private int selCol()
    {
        return PLCCols.SELECTED.col;
    }

    private int borderCol()
    {
        return PLCCols.BORDER.col;
    }

    private int cursorCol()
    {
        return PLCCols.SELECTED.col;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        if (this.visible)
        {
            int col = !this.isFocused() ? PLCCols.INVALID.col : PLCCols.SELECTED.col;
//            fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, col);
            fill(matrices, this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1, 0x90000000);
            GUIUtil.renderBorder(matrices, x, y, width - 1, height - 1, col, 0);

            enableScissor(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height - 1);
            matrices.push();
            matrices.translate(0.0, -this.getScrollY(), 0.0);
            renderHighlightLine(matrices, errorLine, PLCCols.ERROR_LINE.col);
            renderHighlightLine(matrices, debugLine, PLCCols.DEBUG_LINE.col);
            renderContents(matrices, mouseX, mouseY, delta);
            matrices.pop();
            disableScissor();
            renderOverlay(matrices);
        }
    }

    protected void renderHighlightLine(MatrixStack matrices, int line, int col)
    {
        if (line >= 0)
        {
            double lineStart = y + getPadding() + line * lineHeight();
            fill(matrices, x + getPadding(), (int) lineStart, x + width - getPadding(), (int) (lineStart + lineHeight()), col);
        }
    }

    protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        matrices.push();

        float bottom = (float) (y + height - lineHeight() - getPadding());
        textRenderer.draw(matrices, errorMessage, x + getPadding(), bottom, errorCol);

        String string = this.editBox.getText();
//        if (string.isEmpty() && !this.isFocused())
//        {
//            this.textRenderer.drawTrimmed(this.placeholder, this.x + this.getPadding(), this.y + this.getPadding(), this.width - this.getPaddingDoubled(), -857677600);
//        }
//        else

        int i = this.editBox.getCursor();
        boolean bl = this.isFocused() && this.tick / 6 % 2 == 0;
        boolean bl2 = i < string.length();
        float j = 0;
        float k = 0;
        float l = this.y + this.getPadding();

        int var10002;
        for (Iterator<EditBox.Substring> it = this.editBox.getLines().iterator(); it.hasNext(); l += lineHeight())
        {
            matrices.push();
            EditBox.Substring substring = it.next();
            Objects.requireNonNull(this.textRenderer);
            boolean bl3 = this.isVisible((int) (l), (int) (l + lineHeight()));

            matrices.translate(x + getPadding(), l, 0);

            if (bl && bl2 && i >= substring.beginIndex() && i <= substring.endIndex())
            {
                // Render text line with cursor
                if (bl3)
                {
                    matrices.push();
                    matrices.scale(scale, scale, 1);
                    j = this.textRenderer.drawWithShadow(matrices, string.substring(substring.beginIndex(), i), 0, 0, textCol());

                    DrawableHelper.fill(matrices, (int) (j - 1), 0, (int) j, textRenderer.fontHeight, cursorCol());
                    this.textRenderer.drawWithShadow(matrices, string.substring(i, substring.endIndex()), j - 1, 0, textCol());
                    matrices.pop();
                }
            }
            else
            {
                // Render normal text line
                if (bl3)
                {
                    matrices.push();
                    matrices.scale(scale, scale, 1);
                    j = this.textRenderer.drawWithShadow(matrices, string.substring(substring.beginIndex(), substring.endIndex()), 0, 0, textCol()) - 1;
                    matrices.pop();
                }

                k = l;
            }
            matrices.pop();
        }

        if (bl && !bl2)
        {
            if (this.isVisible((int) k, (int) (k + lineHeight())))
            {
                // Render end of text cursor
                matrices.push();
                matrices.translate(0, k, 0);
                matrices.scale(scale, scale, 1);
                DrawableHelper.fill(matrices, (int) (j + 5), 0, (int) j + 10, textRenderer.fontHeight, cursorCol());
                matrices.pop();
            }
        }

        if (this.editBox.hasSelection())
        {
            EditBox.Substring substring2 = this.editBox.getSelection();
            int m = this.x + this.getPadding();
            l = this.y + this.getPadding();

            for (EditBox.Substring substring3 : this.editBox.getLines())
            {
                if (substring2.beginIndex() <= substring3.endIndex())
                {
                    if (substring3.beginIndex() > substring2.endIndex())
                    {
                        break;
                    }

                    if (this.isVisible((int) l, (int) (l + lineHeight())))
                    {
                        int selWidth = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), Math.max(substring2.beginIndex(), substring3.beginIndex())));
                        int o;
                        if (substring2.endIndex() > substring3.endIndex())
                        {
                            o = this.width - this.getPadding();
                        }
                        else
                        {
                            o = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), substring2.endIndex()));
                        }

                        var10002 = m + selWidth;
                        matrices.push();
                        matrices.translate(0, l, 0);
                        this.drawSelection(matrices, var10002, 0, m + o, 9);
                        matrices.pop();
                    }

                }
                l += lineHeight();
            }
        }
        matrices.pop();
    }

    protected void renderOverlay(MatrixStack matrices)
    {
//        super.renderOverlay(matrices);
//        if (this.editBox.hasMaxLength())
//        {
//            int i = this.editBox.getMaxLength();
//            Text text = Text.translatable("gui.multiLineEditBox.character_limit", this.editBox.getText().length(), i);
//            drawTextWithShadow(matrices, this.textRenderer, text, this.x + this.width - this.textRenderer.getWidth(text), this.y + this.height + 4, 10526880);
//        }
    }

    public int getContentsHeight()
    {
        Objects.requireNonNull(this.textRenderer);
        return (int) (lineHeight() * this.editBox.getLineCount());
    }

    protected boolean overflows()
    {
        return (double) this.editBox.getLineCount() > this.getMaxLinesWithoutOverflow();
    }

    protected double getDeltaYPerScroll()
    {
        Objects.requireNonNull(this.textRenderer);
        return lineHeight() / 2.0;
    }

    private void drawSelection(MatrixStack matrices, int left, int top, int right, int bottom)
    {
        matrices.push();
        matrices.scale(scale, scale, 1);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(LogicOp.OR_REVERSE);
        bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.vertex(matrix4f, (float) left, (float) bottom, 0.0F).next();
        bufferBuilder.vertex(matrix4f, (float) right, (float) bottom, 0.0F).next();
        bufferBuilder.vertex(matrix4f, (float) right, (float) top, 0.0F).next();
        bufferBuilder.vertex(matrix4f, (float) left, (float) top, 0.0F).next();
        tessellator.draw();
//        int col = selCol();
//        RenderSystem.setShaderColor(((col >> 24) & 0xFF) / 255f, ((col >> 16) & 0xFF) / 255f, ((col >> 8) & 0xFF) / 255f, (col & 0xFF) / 255f);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();

        matrices.pop();
    }

    private void onCursorChange()
    {
        double d = this.getScrollY();
        EditBox var10000 = this.editBox;
        Objects.requireNonNull(this.textRenderer);
        EditBox.Substring substring = var10000.getLine((int) (d / lineHeight()));
        int var5;
        if (this.editBox.getCursor() <= substring.beginIndex())
        {
            var5 = this.editBox.getCurrentLineIndex();
            Objects.requireNonNull(this.textRenderer);
            d = var5 * lineHeight();
        }
        else
        {
            double var10001 = d + (double) this.height;
            Objects.requireNonNull(this.textRenderer);
            EditBox.Substring substring2 = var10000.getLine((int) (var10001 / lineHeight()) - 1);
            if (this.editBox.getCursor() > substring2.endIndex())
            {
                var5 = this.editBox.getCurrentLineIndex();
                Objects.requireNonNull(this.textRenderer);
                var5 = (int) (var5 * lineHeight() - this.height);
                Objects.requireNonNull(this.textRenderer);
                d = var5 + lineHeight() + this.getPaddingDoubled();
            }
        }

        this.setScrollY(d);
    }

    private double getMaxLinesWithoutOverflow()
    {
        double height1 = this.height - this.getPaddingDoubled();
        return height1 / lineHeight();
    }

    private void moveCursor(double mouseX, double mouseY)
    {
        double d = mouseX - (double) this.x - (double) this.getPadding();
        double e = mouseY - (double) this.y - (double) this.getPadding() + this.getScrollY();
        this.editBox.moveCursor(d / scale, e / scale);
    }

    private double lineHeight()
    {
        return (textRenderer.fontHeight) * scale;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }

    public void setError(String message, int col)
    {
        this.errorMessage = message;
        this.errorCol = col;
    }

    public void setErrorLine(int line)
    {
        this.errorLine = line;
    }

    public void setDebugLine(int line)
    {
        this.debugLine = line;
    }
}
