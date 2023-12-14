package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.api.DataPort;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class NetworkingToolItem extends BaseItem
{
    public NetworkingToolItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings.maxCount(1));
        ItemRegistry.queue(this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        PlayerEntity player = context.getPlayer();
        if (player == null || context.getWorld().isClient())
            return super.useOnBlock(context);

        // Clear data
        if (context.getPlayer().isSneaking())
        {
            context.getStack().removeSubNbt("networking");

            return ActionResult.SUCCESS;
        }

        DataPort port = DataPort.DATA_PORT.find(context.getWorld(), context.getBlockPos(), null);

        if (port != null)
        {
            NbtCompound nbt = context.getStack().getOrCreateSubNbt("networking");

            if (!nbt.contains("first"))
            {
                nbt.put("first", NbtHelper.fromBlockPos(context.getBlockPos()));
                nbt.putString("first_name", context.getWorld().getBlockState(context.getBlockPos()).getBlock().getName().getString());
                BlockPos first = context.getBlockPos();
                context.getPlayer().sendMessage(Text.translatable(getTranslationKey() + ".from", first.getX(), first.getY(), first.getZ()));
                return ActionResult.SUCCESS;
            }
            else
            {
                BlockPos first = NbtHelper.toBlockPos(nbt.getCompound("first"));
                BlockPos second = context.getBlockPos();
                DataPort firstPort = DataPort.DATA_PORT.find(context.getWorld(), first, null);
                if (firstPort != null)
                {
                    firstPort.setTarget(second);
                    context.getPlayer().sendMessage(Text.translatable(getTranslationKey() + ".connected",
                            first.getX(), first.getY(), first.getZ(),
                            second.getX(), second.getY(), second.getZ()));
                    context.getStack().removeSubNbt("networking");
                    return ActionResult.SUCCESS;
                }
            }
        }

        return super.useOnBlock(context);
    }



    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        NbtCompound nbt = itemStack.getSubNbt("networking");
        if (nbt != null)
        {
            String name = nbt.getString("first_name");
            BlockPos pos = NbtHelper.toBlockPos(nbt.getCompound("first"));
            tooltip.add(Text.translatable(getTranslationKey() + ".from", pos.getX(), pos.getY(), pos.getZ()));
        }
    }
}
