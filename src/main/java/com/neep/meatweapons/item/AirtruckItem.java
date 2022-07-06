package com.neep.meatweapons.item;

import com.neep.meatlib.item.IMeatItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatweapons.entity.AbstractVehicleEntity;
import com.neep.meatweapons.entity.AirtruckEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class AirtruckItem extends Item implements IMeatItem
{
    protected String registryName;

    public AirtruckItem(String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        ItemRegistry.queueItem(this);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    public ActionResult useOnBlock(ItemUsageContext context)
    {
        PlayerEntity user = context.getPlayer();
        World world = context.getWorld();
        ItemStack stack = context.getStack();
        Vec3d hit = context.getHitPos();
        if (!world.isClient())
        {
            AbstractVehicleEntity entity = AirtruckEntity.create(world);
            entity.setPosition(hit);
            if (world.isSpaceEmpty(entity))
            {
                world.spawnEntity(entity);
                world.emitGameEvent((Entity)user, GameEvent.ENTITY_PLACE, new BlockPos(hit));
                if (!user.isCreative())
                {
                    stack.decrement(1);
                }
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
        }
        return ActionResult.success(world.isClient);
    }
}
