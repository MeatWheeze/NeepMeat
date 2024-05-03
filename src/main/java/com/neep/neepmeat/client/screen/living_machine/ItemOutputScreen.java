package com.neep.neepmeat.client.screen.living_machine;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.screen_handler.ItemOutputScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ItemOutputScreen extends HandledScreen<ItemOutputScreenHandler>
{
    public static final Identifier TEXTURE = new Identifier(NeepMeat.NAMESPACE, "textures/gui/machine_item_output.png");

    public ItemOutputScreen(ItemOutputScreenHandler handler, PlayerInventory inventory, Text title)
    {
        super(handler, inventory, title);
    }

    @Override
    protected void init()
    {
        backgroundWidth = 176;
        backgroundHeight = 178;
        super.init();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        super.renderBackground(context);
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, 87);

        GUIUtil.drawInventoryBackground(context, x, y + 88);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY)
    {
    }
}
