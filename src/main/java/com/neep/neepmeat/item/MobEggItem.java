package com.neep.neepmeat.item;

import com.neep.meatlib.item.IMeatItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.entity.AbstractVehicleEntity;
import com.neep.meatweapons.entity.AirtruckEntity;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.entity.EggEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class MobEggItem extends Item implements IMeatItem
{
    private final String registryName;

    public MobEggItem(String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        ItemRegistry.queueItem(NeepMeat.NAMESPACE, (IMeatItem) this);
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
                world.emitGameEvent(user, GameEvent.ENTITY_PLACE, new BlockPos(hit));
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
            return new TranslatableText(this.getTranslationKey(), type.getName());
        }
        else return super.getName(stack);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
