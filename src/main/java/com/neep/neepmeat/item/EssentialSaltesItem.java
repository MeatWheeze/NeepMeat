package com.neep.neepmeat.item;

import com.neep.meatlib.item.BaseItem;
import com.neep.meatlib.registry.ItemRegistry;
import dev.architectury.event.events.common.EntityEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class EssentialSaltesItem extends BaseItem
{
    public EssentialSaltesItem(String registryName, int loreLines, Settings settings)
    {
        super(registryName, loreLines, settings);
        ItemRegistry.queueItem(this);
    }

    @Override
    public boolean hasGlint(ItemStack stack)
    {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext)
    {
        super.appendTooltip(itemStack, world, tooltip, tooltipContext);
        String id = itemStack.getOrCreateNbt().getString("id");
        if (id != null)
        {
            tooltip.add(Registry.ENTITY_TYPE.get(new Identifier(id)).getName());
        }
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand)
    {
        storeEntity(stack, entity);
        user.setStackInHand(hand, stack);
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        Entity entity = createEntity(context.getStack(), context.getWorld(), true);

        if (entity != null)
        {
            Vec3d hitPos = context.getHitPos();
            entity.setPos(hitPos.x, hitPos.y + 1, hitPos.z);
            context.getWorld().spawnEntity(entity);
        }

        return ActionResult.SUCCESS;
    }

    public static void storeEntity(ItemStack stack, LivingEntity entity)
    {
        NbtCompound entityCompound = new NbtCompound();
        entity.writeNbt(entityCompound);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.put("entity_data", entityCompound);
        nbt.putString("id", Registry.ENTITY_TYPE.getId(entity.getType()).toString());
        stack.setNbt(nbt);
    }

    public static Entity createEntity(ItemStack stack, World world, boolean exactCopy)
    {
        NbtCompound nbt = stack.getOrCreateNbt();
        String id = nbt.getString("id");
        NbtCompound entityCompound = nbt.getCompound("entity_data");
        if (id != null && entityCompound != null)
        {
            EntityType<? extends Entity> type = Registry.ENTITY_TYPE.get(new Identifier(id));
            Entity entity = type.create(world);
            if (entity != null)
            {
                if (exactCopy)
                {
                    entityCompound.remove(Entity.UUID_KEY);
                    entity.readNbt(entityCompound);
                }
                return entity;
            }
        }
        return null;
    }

    static
    {
    }
}