package com.neep.meatweapons.entity;

import com.neep.meatlib.attachment.player.PlayerAttachment;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.util.GeckoLibUtil;

// A minimal implementation of ItemCooldownManager that works for individual stacks
public class WeaponCooldownAttachment implements PlayerAttachment
{
    public static String ID = "meatweapons:weapon_cooldown";

    protected Int2ObjectOpenHashMap<Entry> map = new Int2ObjectOpenHashMap<>();
    protected int time;

    public static WeaponCooldownAttachment get(PlayerEntity player)
    {
        return player.meatlib$getAttachmentManager().getAttachment(ID);
    }

    public WeaponCooldownAttachment(PlayerEntity player)
    {

    }

    @Override
    public void tickAttachment()
    {
        ++time;
        ObjectIterator<Int2ObjectMap.Entry<Entry>> it = map.int2ObjectEntrySet().fastIterator();
        while (it.hasNext())
        {
            if (it.next().getValue().endTime <= time) it.remove();
        }
    }

    public boolean isCoolingDown(ItemStack stack, int trigger)
    {
        int id = getStackId(stack, trigger);
        Entry entry = map.get(getStackId(stack, trigger));
        return entry != null && entry.endTime > time;
    }

    // Returns a unique ID for each stack (hopefully).
    public static int getStackId(ItemStack stack, int trigger)
    {
        return GeckoLibUtil.getIDFromStack(stack) + trigger;
//        return Objects.hash(stack.getItem().getTranslationKey(), stack.getNbt(), stack.getCount(), trigger);
    }

    public void set(ItemStack stack, int trigger, int cooldown)
    {
        map.put(getStackId(stack, trigger), new Entry(time, time + cooldown));
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