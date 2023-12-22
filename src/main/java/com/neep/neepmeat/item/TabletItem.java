package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.tablet.TabletScreen;
import com.neep.neepmeat.screen_handler.TerminalScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TabletItem extends BaseItem implements NamedScreenHandlerFactory
{
    public TabletItem(String name, Settings settings)
    {
        super(name, 1, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (world.isClient)
        {
            ScreenHandler handler = new TerminalScreenHandler();
            user.currentScreenHandler = handler;
            try
            {
                MinecraftClient.getInstance().setScreen(new TabletScreen(user, handler));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText(NeepMeat.NAMESPACE + "screen.tablet");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return null;
    }
}
