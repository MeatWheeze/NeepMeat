package com.neep.neepmeat.client.screen.plc.edit;

import com.mojang.blaze3d.platform.GlStateManager.LogicOp;
import com.mojang.blaze3d.systems.RenderSystem;
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
    private final Text placeholder;
    private final EditBox editBox;
    private int tick;
    private float scale = 0.8f;

    public EditBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder, Text message)
    {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.placeholder = placeholder;
        this.editBox = new EditBox(textRenderer, width - this.getPaddingDoubled());
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

    protected void renderContents(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        matrices.push();
        matrices.scale(scale, scale, 1);

        String string = this.editBox.getText();
        if (string.isEmpty() && !this.isFocused())
        {
            this.textRenderer.drawTrimmed(this.placeholder, this.x + this.getPadding(), this.y + this.getPadding(), this.width - this.getPaddingDoubled(), -857677600);
        }
        else
        {
            int i = this.editBox.getCursor();
            boolean bl = this.isFocused() && this.tick / 6 % 2 == 0;
            boolean bl2 = i < string.length();
            int j = 0;
            int k = 0;
            int l = this.y + this.getPadding();

            int var10002;
            int var10004;
            for (Iterator<EditBox.Substring> it = this.editBox.getLines().iterator(); it.hasNext(); l += 9)
            {
                EditBox.Substring substring = it.next();
                Objects.requireNonNull(this.textRenderer);
                boolean bl3 = this.isVisible((int) (l * scale), (int) (l * scale + lineHeight()));
                if (bl && bl2 && i >= substring.beginIndex() && i <= substring.endIndex())
                {
                    if (bl3)
                    {
                        j = this.textRenderer.drawWithShadow(matrices, string.substring(substring.beginIndex(), i), (float) (this.x + this.getPadding()), (float) l, -2039584) - 1;
                        var10002 = l - 1;
                        int var10003 = j + 1;
                        var10004 = l + 1;
                        Objects.requireNonNull(this.textRenderer);
                        DrawableHelper.fill(matrices, j, var10002, var10003, var10004 + 9, -3092272);
                        this.textRenderer.drawWithShadow(matrices, string.substring(i, substring.endIndex()), (float) j, (float) l, -2039584);
                    }
                }
                else
                {
                    if (bl3)
                    {
                        j = this.textRenderer.drawWithShadow(matrices, string.substring(substring.beginIndex(), substring.endIndex()), (float) (this.x + this.getPadding()), (float) l, -2039584) - 1;
                    }

                    k = l;
                }
            }

            if (bl && !bl2)
            {
                if (this.isVisible(k, k + 9))
                {
                    this.textRenderer.drawWithShadow(matrices, "_", (float) j, (float) k, -3092272);
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

                        if (this.isVisible(l, l + 9))
                        {
                            int n = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), Math.max(substring2.beginIndex(), substring3.beginIndex())));
                            int o;
                            if (substring2.endIndex() > substring3.endIndex())
                            {
                                o = this.width - this.getPadding();
                            }
                            else
                            {
                                o = this.textRenderer.getWidth(string.substring(substring3.beginIndex(), substring2.endIndex()));
                            }

                            var10002 = m + n;
                            var10004 = m + o;
                            Objects.requireNonNull(this.textRenderer);
                            this.drawSelection(matrices, var10002, l, var10004, l + 9);
                        }

                    }
                    Objects.requireNonNull(this.textRenderer);
                    l += 9;
                }
            }

        }
        matrices.pop();
    }

    protected void renderOverlay(MatrixStack matrices)
    {
        super.renderOverlay(matrices);
        if (this.editBox.hasMaxLength())
        {
            int i = this.editBox.getMaxLength();
            Text text = Text.translatable("gui.multiLineEditBox.character_limit", this.editBox.getText().length(), i);
            drawTextWithShadow(matrices, this.textRenderer, text, this.x + this.width - this.textRenderer.getWidth(text), this.y + this.height + 4, 10526880);
        }

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
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
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
        return textRenderer.fontHeight * scale;
    }
}
