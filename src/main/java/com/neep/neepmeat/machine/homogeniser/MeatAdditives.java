package com.neep.neepmeat.machine.homogeniser;

import com.google.common.collect.Maps;
import com.neep.neepmeat.api.processing.MeatFluidUtil;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class MeatAdditives
{
    private static final MeatAdditives INSTANCE = new MeatAdditives();


    public static MeatAdditives getInstance()
    {
        return INSTANCE;
    }

    private final Map<Item, Entry> map = Maps.newHashMap();

    public static void register(Item item, Entry entry)
    {
        getInstance().map.put(item, entry);
    }

    public Entry get(Item item)
    {
        return map.get(item);
    }

    public static void init()
    {
        register(Items.SUGAR, new HungerAdditive(1, FluidConstants.BUCKET));
    }

    public interface Entry
    {
        long getAmount();
        boolean canApply(FluidVariant variant);

        FluidVariant apply(FluidVariant variant);
    }

    public static class HungerAdditive implements Entry
    {
        private final float hunger;
        private final long amount;

        public HungerAdditive(float hunger, long amount)
        {
            this.hunger = hunger;
            this.amount = amount;
        }

        @Override
        public long getAmount()
        {
            return amount;
        }

        @Override
        public boolean canApply(FluidVariant variant)
        {
            return variant.isOf(NMFluids.STILL_C_MEAT);
        }

        @Override
        public FluidVariant apply(FluidVariant variant)
        {
            NbtCompound newNbt = new NbtCompound();
            if (MeatFluidUtil.getRoot(variant) != null)
            {
                MeatFluidUtil.copyRoot(variant.getNbt(), newNbt);
            }

            MeatFluidUtil.setHunger(newNbt, MeatFluidUtil.getHunger(variant) + hunger);


            return FluidVariant.of(variant.getFluid(), newNbt);
        }
    }

}
