package com.neep.neepmeat.api.processing;

import com.neep.neepmeat.fluid.ore_fat.OreFatFluidFactory;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class OreFatRegistry
{
    protected static Map<Item, Entry> ENTRIES = new HashMap<>();

    public static void init()
    {
        register(Items.RAW_IRON, 0xfedec8, Items.IRON_INGOT);
        register(Items.RAW_GOLD, 0xfaea2e, Items.GOLD_INGOT);
        register(Items.RAW_COPPER, 0x4fba98, Items.COPPER_INGOT);
    }

    public static void register(Item item, Integer col, Item result)
    {
        ENTRIES.put(item, new Entry(item, col, result));
    }

    public static Entry get(Item item)
    {
        return ENTRIES.get(item);
    }

    public static Entry get(Identifier id)
    {
        return ENTRIES.get(Registry.ITEM.get(id));
    }

    public static Entry getFromVariant(FluidVariant variant)
    {
        if (variant.getObject() instanceof OreFatFluidFactory.Main)
        {
            NbtCompound nbt = variant.getNbt();
            String string;
            if (nbt != null && (string = nbt.getString("item")) != null)
            {
                Identifier itemId = new Identifier(string);
                return get(itemId);
            }
        }
        return null;
    }

    public static Item getItem(FluidVariant variant)
    {
        NbtCompound nbt = variant.getNbt();
        if (variant.getObject() instanceof OreFatFluidFactory.Main && nbt != null)
        {
            return Registry.ITEM.get(new Identifier((nbt.getString("item"))));
        }
        return null;
    }

    public static FluidVariant getDirty(Item ore)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item", Registry.ITEM.getId(ore).toString());
        return FluidVariant.of(NMFluids.STILL_DIRTY_ORE_FAT, nbt);
    }

    public static FluidVariant getClean(Item ore)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("item", Registry.ITEM.getId(ore).toString());
        return FluidVariant.of(NMFluids.STILL_CLEAN_ORE_FAT, nbt);
    }

    public record Entry(Item source, Integer col, Item result)
    {

    }
}
