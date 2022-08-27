package com.neep.neepmeat.screen_handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;

import java.util.function.Consumer;

public class DummyScreenHandler extends ScreenHandler
{
    protected final Consumer<Inventory> stackChangeCallback;

    public DummyScreenHandler(Consumer<Inventory> stackChanged)
    {
        super(null, 0);
        this.stackChangeCallback = stackChanged;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return false;
    }

    @Override
    public void onContentChanged(Inventory inventory)
    {
        stackChangeCallback.accept(inventory);
    }
}
