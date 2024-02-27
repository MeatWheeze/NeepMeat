package com.neep.meatweapons.entity;

import com.neep.meatlib.attachment.player.PlayerAttachment;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoItem;

// A minimal implementation of ItemCooldownManager that works for individual stacks
public class WeaponCooldownAttachment implements PlayerAttachment
{
    public static String ID = "meatweapons:weapon_cooldown";
    private final PlayerEntity player;

    protected Long2ObjectOpenHashMap<Entry> map = new Long2ObjectOpenHashMap<>();
    protected int time;

    public static WeaponCooldownAttachment get(PlayerEntity player)
    {
        return player.meatlib$getAttachmentManager().getAttachment(ID);
    }

    public WeaponCooldownAttachment(PlayerEntity player)
    {

        this.player = player;
    }

    @Override
    public void tickAttachment()
    {
        ++time;
        ObjectIterator<Long2ObjectMap.Entry<Entry>> it = map.long2ObjectEntrySet().fastIterator();
        while (it.hasNext())
        {
            if (it.next().getValue().endTime <= time) it.remove();
        }
    }

    public boolean isCoolingDown(ItemStack stack, int trigger)
    {
        long id = getStackId(stack, trigger, player.getWorld());
        Entry entry = map.get(getStackId(stack, trigger, player.getWorld()));
        return entry != null && entry.endTime > time;
    }

    // Returns a unique ID for each stack (hopefully).
    public static long getStackId(ItemStack stack, int trigger, World world)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            return GeoItem.getOrAssignId(stack, serverWorld);
        }
        return GeoItem.getId(stack) + trigger;
//        return Objects.hash(stack.getItem().getTranslationKey(), stack.getNbt(), stack.getCount(), trigger);
    }

    public void set(ItemStack stack, int trigger, int cooldown)
    {
        map.put(getStackId(stack, trigger, player.getWorld()), new Entry(time, time + cooldown));
    }

    protected static class Entry
    {
        final int startTime;
        final int endTime;

        Entry(int start, int end)
        {
            this.startTime = start;
            this.endTime = end;
        }

    }
}