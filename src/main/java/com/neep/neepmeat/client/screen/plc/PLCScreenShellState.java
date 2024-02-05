package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.api.plc.instruction.SimpleInstructionProvider;
import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.plc.edit.InstructionBrowserWidget;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;

public class PLCScreenShellState extends ScreenSubElement implements Drawable, Element, Selectable, PLCScreenState
{
    private final PLCProgramScreen parent;
    private final InstructionBrowserWidget browser;
    private InstructionProvider selectedProvider;

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

        addDrawableChild(browser);
    }

    private void selectProvider(InstructionProvider provider)
    {
        PLCSyncProgram.Client.switchOperation(provider, parent.getScreenHandler().getPlc());
        this.selectedProvider = provider;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        super.render(matrices, mouseX, mouseY, delta);
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
        PLCSyncProgram.Client.sendArgument(argument, parent.getScreenHandler().getPlc());
    }

    @Override
    public void onKeyPressed(int keyCode, int scanCode, int modifiers) { keyPressed(keyCode, scanCode, modifiers); }

    @Override
    public boolean isSelected()
    {
        return false;
    }
}
