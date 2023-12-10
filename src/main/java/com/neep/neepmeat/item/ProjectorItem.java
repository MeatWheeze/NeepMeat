package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.tablet.GuideMainScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

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
        tooltip.add(Text.translatable(item.getTranslationKey() + ".lore_0").formatted(Formatting.YELLOW));
    }

//    @Nullable
//    @Override
//    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
//    {
//        return new GuideScreenHandler(syncId, inv);
//    }

    @Environment(EnvType.CLIENT)
    private static class Client
    {
        public static void openScreen()
        {
            MinecraftClient.getInstance().setScreen(new GuideMainScreen());
        }
    }
}
