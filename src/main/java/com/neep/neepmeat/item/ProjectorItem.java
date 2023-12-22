package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.screen_handler.GuideScreenHandler;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProjectorItem extends BaseItem implements NamedScreenHandlerFactory
{
    public ProjectorItem(String name, Settings settings)
    {
        super(name, 0, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        if (!world.isClient())
        {
            user.openHandledScreen(this);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText(NeepMeat.NAMESPACE + "screen.guide");
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        tooltip.add(new TranslatableText(getTranslationKey() + ".lore_0").formatted(Formatting.YELLOW));
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new GuideScreenHandler(syncId, inv);
    }
}
