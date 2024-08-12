package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.neepbus.screen.InteractiveControlScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class InteractiveControlScreen extends HandledScreen<InteractiveControlScreenHandler>
{
    // Entirely transparent. Uses for processing input events.
    public InteractiveControlScreen(InteractiveControlScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {

    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
//        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_E)
//            close();

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY)
    {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount)
    {
        handler.mouseScrollC2S.emitter().scroll(amount);
        return true;
    }
}
