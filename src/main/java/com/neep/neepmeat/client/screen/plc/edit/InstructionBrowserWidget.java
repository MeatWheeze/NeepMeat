package com.neep.neepmeat.client.screen.plc.edit;

import com.google.common.collect.Lists;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.plc.PLCProgramScreen;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.instruction.gui.InstructionAttributes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class InstructionBrowserWidget implements Element, Drawable, ParentElement, Selectable
{
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = client.textRenderer;
    private final PLCProgramScreen parent;

    private float scrollAmount = 0;

    private int x, y;
    private int width, height;
    private int screenWidth, screenHeight;

    private final List<OperationWidget> entries = Lists.newArrayList();

    public InstructionBrowserWidget(PLCProgramScreen parent)
    {
        this.parent = parent;
    }

    public void init(int screenWidth, int screenHeight)
    {
        children.clear();

        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        this.width = 100;
        this.height = 200;
        this.x = screenWidth - width;
        this.y = screenHeight - height;
        addEntries();
    }

    protected void addEntries()
    {
        entries.clear();

        List<? extends InstructionProvider> instructions;
        instructions = Instructions.REGISTRY.stream().toList();

        for (var provider : instructions)
        {
            int elementWidth = width - 3;
            OperationWidget widget = new OperationWidget(elementWidth - 4, provider, p -> {});
            entries.add(widget);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        Screen.fill(matrices, x, y, x + width, y + height, 0x90000000);
        GUIUtil.renderBorder(matrices, x, y, width - 1, height - 1, PLCCols.BORDER.col, 0);

        matrices.push();
        int yOffset = (int) (y + 2 + scrollAmount);
        int xOffset = x + 2;

        for (var entry : entries)
        {
            if (yOffset >= y && yOffset + entry.height < y + height)
                entry.render(matrices, xOffset, yOffset, mouseX, mouseY, delta);
            yOffset += entry.height();
        }
        matrices.pop();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (isInBounds(mouseX, mouseY))
        {
            scrollAmount = (float) Math.min(0.0f, scrollAmount + amount * 4f);

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isInBounds(mouseX, mouseY))
        {
            ParentElement.super.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY)
    {
        return isInBounds(mouseX, mouseY);
    }

    public boolean isInBounds(double mx, double my)
    {
        return (x <= mx && y <= my && x + width >= mx && y + height >= my);
    }

    private final List<Element> children = Lists.newArrayList();
    @Nullable private Element focused;

    @Override
    public List<? extends Element> children()
    {
        return children;
    }

    @Override
    public boolean isDragging()
    {
        return false;
    }

    @Override
    public void setDragging(boolean dragging)
    {

    }

    @Nullable
    @Override
    public Element getFocused()
    {
        return focused;
    }

    @Override
    public void setFocused(@Nullable Element focused)
    {
        this.focused = focused;
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

    public class OperationWidget extends DrawableHelper
    {
        private final InstructionProvider provider;
        private final Consumer<InstructionProvider> action;
        private final int width, height;
        private final Text message;

        public OperationWidget(int width, InstructionProvider provider, Consumer<InstructionProvider> action)
        {
//            super(x, y, width, textRenderer.fontHeight + 1, provider.getShortName());
            this.message = provider.getShortName();
            this.width = width; this.height = textRenderer.fontHeight + 1;
            this.provider = provider;
            this.action = action;
        }

        public void render(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta)
        {
//            int col = PLCOperationSelector.this.instructionProvider == provider ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
            int col = PLCCols.BORDER.col;
//            GUIUtil.renderBorder(matrices, x, y, width, height - 1, col, 0);
            GUIUtil.drawHorizontalLine1(matrices, x, x + width, y, col);

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(matrices, message, x + 2, y + 2, col);
            if (isMouseOver(x, y, mouseX, mouseY))
            {
                renderTooltip(matrices, x, y, mouseX, mouseY);
            }
        }

        private boolean isMouseOver(int x, int y, int mouseX, int mouseY)
        {
            return (x <= mouseX && y <= mouseY && x + this.width >= mouseX && y + this.height >= mouseY);
        }

        public void renderTooltip(MatrixStack matrices, int x, int y, int mouseX, int mouseY)
        {
            InstructionAttributes.InstructionTooltip tooltip = InstructionAttributes.get(provider);
            int width = 100;
            int tx = x - width - 5;
            int ty = y;
            if (tooltip != InstructionAttributes.InstructionTooltip.EMPTY)
            {
                List<OrderedText> wrapped = textRenderer.wrapLines(tooltip.description(), width);
                parent.renderTooltipOrderedText(matrices, wrapped, false, tx, ty, width, PLCCols.TEXT.col);
            }
        }

        public void onClick(double mouseX, double mouseY)
        {
//            super.onClick(mouseX, mouseY);
            action.accept(provider);
        }

        public void playDownSound(SoundManager soundManager)
        {
            client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1.0f));
        }

        public int height()
        {
            return height;
        }
    }
}
