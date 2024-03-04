package com.neep.neepmeat.item;

import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.EggEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobEggItem extends Item implements MeatlibItem
{
    private final String registryName;
    private final TooltipSupplier tooltip;

    public MobEggItem(String registryName, TooltipSupplier tooltip, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.tooltip = tooltip;
        ItemRegistry.queue(NeepMeat.NAMESPACE, (MeatlibItem) this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        PlayerEntity user = context.getPlayer();
        World world = context.getWorld();
        ItemStack stack = context.getStack();
        Vec3d hit = context.getHitPos();
        if (!world.isClient())
        {
            EntityType<?> hatchType = EssentialSaltesItem.getEntityType(stack);
            EggEntity egg = new EggEntity(world, hatchType);
            egg.setPosition(hit);
            if (world.isSpaceEmpty(egg))
            {
                world.spawnEntity(egg);
//                world.emitGameEvent(user, GameEvent.ENTITY_PLACE, BlockPos.ofFloored(hit));
                world.emitGameEvent(user, GameEvent.ENTITY_PLACE, BlockPos.ofFloored(hit));
                if (!user.isCreative())
                {
                    stack.decrement(1);
                }
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public Text getName(ItemStack stack)
    {
        EntityType<?> type = EssentialSaltesItem.getEntityType(stack);
        if (type != null)
        {
            return Text.translatable(this.getTranslationKey(), type.getName());
        }
        else return super.getName(stack);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        super.appendTooltip(stack, world, tooltip, context);
        this.tooltip.apply(this, tooltip);
    }
}
