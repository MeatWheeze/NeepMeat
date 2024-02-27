package com.neep.neepmeat.client.screen.plc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.api.plc.instruction.SimpleInstructionProvider;
import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.plc.edit.InstructionBrowserWidget;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.network.plc.PLCSyncThings;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.windows.WINDOWPLACEMENT;

import java.util.List;

public class PLCScreenShellState extends ScreenSubElement implements Drawable, Element, Selectable, PLCScreenState
{
    private final PLCProgramScreen parent;
    private final InstructionBrowserWidget browser;
    private InstructionProvider selectedProvider;
    private @Nullable Text error;

    public PLCScreenShellState(PLCProgramScreen parent)
    {
        this.parent = parent;
        browser = new InstructionBrowserWidget(this.parent, () -> selectedProvider, this::validProvider, this::selectProvider);
    }

    private boolean validProvider(InstructionProvider provider)
    {
        return provider instanceof SimpleInstructionProvider;
    }

    @Override
    protected void init()
    {
        super.init();

        browser.init(screenWidth, screenHeight);
        addDrawable(new HelpWidget(1, 1));
        addDrawable(new CurrentArgumentWidget(browser.getX(), browser.getY() - textRenderer.fontHeight - 2, parent.getScreenHandler()));

        addDrawableChild(browser);
    }

    private void selectProvider(InstructionProvider provider)
    {
        setError(null);
        PLCSyncThings.Client.switchOperation(provider, parent.getScreenHandler().getPlc());
        this.selectedProvider = provider;
    }

    @Override
    public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);

        if (error != null)
        {
            int ex = 2;
            int ey = screenHeight - textRenderer.fontHeight - 2;
            GUIUtil.drawText(matrices, textRenderer, error, ex, ey, PLCCols.SELECTED.col, true);
        }
    }

    @Override
    public void tick()
    {
        super.tick();
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

    @Override
    public void argument(Argument argument)
    {
        setError(null);
        PLCSyncThings.Client.sendArgument(argument, parent.getScreenHandler().getPlc());
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) { keyPressed(keyCode, scanCode, modifiers); }

    @Override
    public boolean isSelected()
    {
        return false;
    }

    public void setError(@Nullable Text text)
    {
        this.error = text;
    }

    public class HelpWidget implements Drawable
    {
        private final int x;
        private final int y;
        private final int width = 16;
        private final int height = 16;
        private final Text text;

        public HelpWidget(int x, int y)
        {
            this.x = x;
            this.y = y;
            this.text = Text.translatable("text." + NeepMeat.NAMESPACE + ".plc.interactive_help");
        }

        private boolean isMouseOver(double mx, double my)
        {
            return (mx > x && mx < x + width && my > y && my < y + height);
        }

        @Override
        public void render(DrawContext matrices, int mouseX, int mouseY, float delta)
        {
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderTexture(0, PLCProgramScreen.WIDGETS);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            int u = 112;
            int v = isMouseOver(mouseX, mouseY) ? 32 : 16;
            matrices.drawTexture(PLCProgramScreen.WIDGETS, this.x, this.y, 0, u, v, width, height, 256, 256);

            int maxWidth = 150;

            List<OrderedText> lines = textRenderer.wrapLines(text, maxWidth);

            if (isMouseOver(mouseX, mouseY))
            {
                parent.renderTooltipOrderedText(matrices, lines, false, mouseX, mouseY, maxWidth, PLCCols.TEXT.col);
            }
        }
    }
}
