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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class InstructionBrowserWidget implements Element, Drawable, ParentElement, Selectable
{
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TextRenderer textRenderer = client.textRenderer;
    private final PLCProgramScreen parent;
    private final Supplier<InstructionProvider> selected;
    private final Predicate<InstructionProvider> predicate;
    private final Consumer<InstructionProvider> action;

    private float scrollAmount = 0;

    private int x, y;
    private int width, height;
    private int screenWidth, screenHeight;
    private final int pad = 1;

    private final List<DropWidget> entries = Lists.newArrayList();

    public InstructionBrowserWidget(PLCProgramScreen parent, Supplier<InstructionProvider> selected, Predicate<InstructionProvider> predicate, Consumer<InstructionProvider> action)
    {
        this.parent = parent;
        this.selected = selected;
        this.predicate = predicate;
        this.action = action;
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

        if (entries.isEmpty())
        {
            addEntries();
        }
    }

    protected void addEntries()
    {
        entries.clear();

        int elementWidth = width - 3;
        Instructions.REGISTRY.stream()
                .filter(predicate)
                .collect(Collectors.groupingBy(p -> InstructionAttributes.get(p).category()))
                .forEach((category, list) ->
        {
            entries.add(new DropWidget(elementWidth, category, list.stream().map(p -> new OperationWidget(elementWidth - 2, p, action)).collect(Collectors.toList())));
        });
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        Screen.fill(matrices, x, y, x + width, y + height, 0x90000000);
        GUIUtil.renderBorder(matrices, x, y, width - 1, height - 1, PLCCols.BORDER.col, 0);

        matrices.push();
        int yOffset = (int) (y + 2 + scrollAmount);
        int xOffset = x + 2;

        enableScissor();
        for (var entry : entries)
        {
            yOffset += entry.render(matrices, xOffset, yOffset, mouseX, mouseY, delta) + pad;
        }
        disableScissor();
        matrices.pop();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        if (isInBounds(mouseX, mouseY))
        {
            scrollAmount = (float) Math.min(0.0f, scrollAmount + amount * 6f);

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (isInBounds(mouseX, mouseY))
        {
            for (var entry : entries)
            {
                entry.onClick(mouseX, mouseY);
            }
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

    private void enableScissor()
    {
        DrawableHelper.enableScissor(x + 1, y + 1, x + width - 1, y + height - 1);
    }

    private void disableScissor()
    {
        DrawableHelper.disableScissor();
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    private class DropWidget extends DrawableHelper
    {
        private final InstructionAttributes.Category category;
        private final List<OperationWidget> operationWidgets;
        private final int width;
        private final int headerHeight = textRenderer.fontHeight + 4;
        private boolean unfolded = false;

        private int prevX;
        private int prevY;

        public DropWidget(int elementWidth, InstructionAttributes.Category category, List<OperationWidget> operationWidgets)
        {
            this.width = elementWidth;
            this.category = category;
            this.operationWidgets = operationWidgets;
        }

        public float render(MatrixStack matrices, int x, int y, double mouseX, double mouseY, float delta)
        {
            prevX = x;
            prevY = y;

            String arrow = unfolded ? "↓" : "→";
            textRenderer.draw(matrices, arrow, x + 2, y + 2, PLCCols.TEXT.col);
            textRenderer.draw(matrices, category.name, x + 2 + 9, y + 2, PLCCols.TEXT.col);

            int yOffset = headerHeight;
            if (unfolded)
            {
                for (var widget : operationWidgets)
                {
                    widget.render(matrices, x, y + yOffset, mouseX, mouseY, delta);
                    yOffset += widget.height + 1;
                }
            }
            return yOffset;
        }

        private boolean isMouseOver(int x, int y, double mouseX, double mouseY)
        {
            return x <= mouseX && y <= mouseY && x + this.width >= mouseX && y + headerHeight >= mouseY;
        }

        public void onClick(double mouseX, double mouseY)
        {
            if (isMouseOver(prevX, prevY, mouseX, mouseY))
            {
                unfolded = !unfolded;
                client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1));
            }

            if (unfolded)
            {
                operationWidgets.forEach(w ->
                {
                    if (w.isMouseOver(mouseX, mouseY))
                        w.onClick(mouseX, mouseY);
                });
            }
        }
    }

    private class OperationWidget extends DrawableHelper
    {
        private final InstructionProvider provider;
        private final Consumer<InstructionProvider> action;
        private final int width, height;
        private final Text message;

        private int prevX;
        private int prevY;

        public OperationWidget(int width, InstructionProvider provider, Consumer<InstructionProvider> action)
        {
            this.message = provider.getShortName();
            this.width = width; this.height = textRenderer.fontHeight + 1;
            this.provider = provider;
            this.action = action;
        }

        public void render(MatrixStack matrices, int x, int y, double mouseX, double mouseY, float delta)
        {
            this.prevX = x;
            this.prevY = y;

            int col = selected.get() == provider ? PLCCols.SELECTED.col : PLCCols.BORDER.col;
            GUIUtil.drawHorizontalLine1(matrices, x, x + width, y, col);

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(matrices, message, x + 2, y + 2, col);
            if (isMouseOver(mouseX, mouseY))
            {
                renderTooltip(matrices, x, y, (int) mouseX, (int) mouseY);
            }
        }

        private boolean isMouseOver(double mouseX, double mouseY)
        {
            return InstructionBrowserWidget.this.isMouseOver(mouseX, mouseY) &&
                    (prevX<= mouseX && prevY <= mouseY && prevX + this.width >= mouseX && prevY + this.height >= mouseY);
        }

        public void renderTooltip(MatrixStack matrices, int x, int y, int mouseX, int mouseY)
        {
            InstructionBrowserWidget.this.disableScissor();
            InstructionAttributes.InstructionTooltip tooltip = InstructionAttributes.get(provider);
            int width = 200;
            int tx = x - width - 5;
            if (tooltip != InstructionAttributes.InstructionTooltip.EMPTY)
            {
                List<OrderedText> wrapped = textRenderer.wrapLines(tooltip.description(), width);
                parent.renderTooltipOrderedText(matrices, wrapped, false, tx, y, width, PLCCols.TEXT.col);
            }
            InstructionBrowserWidget.this.enableScissor();
        }

        public void onClick(double mouseX, double mouseY)
        {
            action.accept(provider);
            playDownSound(client.getSoundManager());
        }

        public void playDownSound(SoundManager soundManager)
        {
            soundManager.play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1.0f));
        }

        public int height()
        {
            return height;
        }
    }
}
