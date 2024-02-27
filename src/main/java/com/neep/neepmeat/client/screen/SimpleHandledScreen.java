package com.neep.neepmeat.client.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class SimpleHandledScreen<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T>
{
    protected final T handler;

    protected SimpleHandledScreen(T handler, Text title)
    {
        super(title);
        this.handler = handler;
    }

    @Override
    public T getScreenHandler()
    {
        return handler;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        }
        if (client.options.inventoryKey.matchesKey(keyCode, scanCode))
        {
            this.close();
            return true;
        }
        return true;
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }

    @Override
    public final void tick()
    {
        super.tick();
        if (!client.player.isAlive() || client.player.isRemoved())
        {
            client.player.closeHandledScreen();
        }
    }

    @Override
    public void removed()
    {
        if (client.player == null)
        {
            return;
        }
        this.handler.onClosed(client.player);
    }

    @Override
    public void close()
    {
        client.player.closeHandledScreen();
        close();
    }
}
