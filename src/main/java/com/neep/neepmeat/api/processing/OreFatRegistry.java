package com.neep.neepmeat.api.processing;

import com.neep.neepmeat.fluid.ore_fat.OreFatFluidFactory;
import com.neep.neepmeat.init.NMFluids;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class OreFatRegistry
{
    protected static List<Entry> ENTRIES = new LinkedList<>();

    public static void init()
    {
        register(ConventionalItemTags.RAW_IRON_ORES, "Iron", 0xfedec8, Items.IRON_INGOT);
        register(ConventionalItemTags.RAW_GOLD_ORES, "Gold", 0xfaea2e, Items.GOLD_INGOT);
        register(ConventionalItemTags.RAW_COPPER_ORES, "Copper", 0x4fba98, Items.COPPER_INGOT);

        if (FabricLoader.getInstance().isModLoaded("modern_industrialization"))
        {
            register(TagKey.of(Registry.ITEM.getKey(), new Identifier("c:raw_lead_ores")), "Lead", 0x7188ca, Registry.ITEM.get(new Identifier("modern_industrialization:lead_ingot")));
            register(TagKey.of(Registry.ITEM.getKey(), new Identifier("c:raw_nickel_ores")), "Nickel", 0xe5e5b7, Registry.ITEM.get(new Identifier("modern_industrialization:nickel_ingot")));
            register(TagKey.of(Registry.ITEM.getKey(), new Identifier("c:raw_silver_ores")), "Silver", 0x94aad3, Registry.ITEM.get(new Identifier("modern_industrialization:silver_ingot")));
            register(TagKey.of(Registry.ITEM.getKey(), new Identifier("c:raw_tin_ores")), "Tin", 0xe2d9f2, Registry.ITEM.get(new Identifier("modern_industrialization:tin_ingot")));
            register(TagKey.of(Registry.ITEM.getKey(), new Identifier("c:raw_antimony_ores")), "Antimony", 0x80808c, Registry.ITEM.get(new Identifier("modern_industrialization:antimony_ingot")));
        }
    }

    // Life's too short to munge around with translation keys
    public static void register(TagKey<Item> tag, String name, Integer col, Item result)
    {
        ENTRIES.add(new Entry(tag, Text.of(name), col, result));
    }

    public static Entry getFromInput(Item item)
    {
        return ENTRIES.stream().filter(e -> item.getDefaultStack().isIn(e.tag)).findFirst().orElse(null);
    }

    public static Entry getFromOutput(Item item)
    {
        return ENTRIES.stream().filter(e -> e.result.equals(item)).findFirst().orElse(null);
    }

    public static Entry getFromOutput(Identifier id)
    {
        return ENTRIES.stream().filter(e -> e.result.equals(Registry.ITEM.get(id))).findFirst().orElse(null);
    }

    public static Entry getFromInput(Identifier id)
    {
        return getFromInput(Registry.ITEM.get(id));
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
                return getFromOutput(itemId);
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

    public record Entry(TagKey<Item> tag, Text name, int col, Item result)
    {
        public NbtCompound toNbt()
        {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("item", Registry.ITEM.getId(result).toString());
            return nbt;
        }
    }

//    public abstract static class Entry
//    {
//        Item result;
//        int col;
//
//        public Entry(int col, Item result)
//        {
//            this.col = col;
//            this.result = result;
//        }
//
//        public Item result() { return result; }
//        public int col() { return col; }
//    }
//
//    public static class ItemEntry extends Entry
//    {
//        public ItemEntry(Item source, int col, Item result)
//        {
//            super(col, result);
//        }
//    }
//
//    public static class TagEntry extends Entry
//    {
//        public TagEntry(int col, Item result)
//        {
//            super(col, result);
//        }
//    }
}
