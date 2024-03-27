package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.client.screen.tablet.GuideMainScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import javax.tools.Tool;
import java.util.List;

public class ProjectorItem extends BaseItem
{
    public ProjectorItem(String name, Settings settings)
    {
        super(name, ProjectorItem::applyTooltip, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (world.isClient())
        {
            Client.openScreen();
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

//    @Override
//    public Text getDisplayName()
//    {
//        return Text.translatable(NeepMeat.NAMESPACE + "screen.guide");
//    }

    public static void applyTooltip(Item item, List<Text> tooltip)
    {
        TooltipSupplier.wrapLines(tooltip, Text.translatable(item.getTranslationKey() + ".lore_0").formatted(Formatting.YELLOW));
        TooltipSupplier.wrapLines(tooltip, Text.translatable(item.getTranslationKey() + ".lore_1").formatted(Formatting.GRAY));
    }

//    @Nullable
//    @Override
//    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
//    {
//        return new GuideScreenHandler(syncId, inv);
//    }


    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player)
    {
        return super.onStackClicked(stack, slot, clickType, player);
//        return true;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference)
    {
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
//        return true;
    }

    @Environment(EnvType.CLIENT)
    public static class Client
    {
        public static GuideMainScreen openScreen()
        {
            MinecraftClient client = MinecraftClient.getInstance();
            GuideMainScreen screen = new GuideMainScreen();

            if (client.player != null)
                client.player.closeHandledScreen();

            client.setScreen(screen);
            return screen;
        }
    }
}
