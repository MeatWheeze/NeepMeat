package com.neep.neepmeat.neepbus.client.screen;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.util.GUIUtil;
import com.neep.neepmeat.neepbus.screen.SliderScreenHandler;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SliderScreen extends HandledScreen<SliderScreenHandler>
{
    private final RangeConfigWidget rangeConfig;

    public SliderScreen(SliderScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);

        rangeConfig = new RangeConfigWidget(6, 0, 100, 100, list -> handler.receiveParamsC2S.emitter().update(list));
        handler.updateParamsS2C.receiver(rangeConfig::updateParams);
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
    protected void init()
    {
        super.init();

        int wHeight = client.getWindow().getScaledHeight();

        addDrawableChild(rangeConfig);

        // Attach the config box to the lower left
        rangeConfig.init();
        rangeConfig.setPos(rangeConfig.x(), wHeight - rangeConfig.h() - 6);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        int yOff = 5;
        for (var text : textRenderer.wrapLines(Text.of("Use W and S or scroll to change value.\nHold SHIFT to use 1/10 divisions.\nPress ESC to exit."), 400))
        {
            GUIUtil.drawText(context, textRenderer, text, 5, yOff, PLCCols.TEXT.col, true);
            yOff += textRenderer.fontHeight;
        }
    }

    @Override
    protected void handledScreenTick()
    {
        super.handledScreenTick();

        rangeConfig.tick();

        // Query keys every tick to allow continuous slider movement.
        Window window = client.getWindow();
        if (GLFW.glfwGetKey(window.getHandle(), KeyBindingHelper.getBoundKeyOf(client.options.forwardKey).getCode())
                == GLFW.GLFW_PRESS)
        {
            handler.incrementC2S.emitter().scroll(1, !Screen.hasShiftDown());
        }
        else if (GLFW.glfwGetKey(window.getHandle(), KeyBindingHelper.getBoundKeyOf(client.options.backKey).getCode())
                == GLFW.GLFW_PRESS)
        {
            handler.incrementC2S.emitter().scroll(-1, !Screen.hasShiftDown());
        }
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
        handler.incrementC2S.emitter().scroll(amount, !Screen.hasShiftDown());
        return true;
    }
}
