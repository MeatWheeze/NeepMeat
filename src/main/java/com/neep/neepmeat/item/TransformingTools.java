package com.neep.neepmeat.item;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class TransformingTools
{
    public static final String ROOT_ID = "neepmeat:transformingTool";

    public static boolean isTransformingTool(ItemStack stack)
    {
        return stack.getOrCreateNbt().contains(ROOT_ID);
    }

    public static boolean isTransformingTool(ItemVariant variant)
    {
        NbtCompound nbt = variant.getNbt();
        return nbt != null && nbt.contains(ROOT_ID);
    }

    public static void swap(ItemStack oldStack, PlayerEntity player)
    {
        if (isTransformingTool(oldStack))
        {
            NbtCompound nbt = oldStack.getOrCreateNbt().getCompound(ROOT_ID);

            // Retrieve alternate stack from NBT
            ItemStack newStack = ItemStack.fromNbt(nbt);
            NbtCompound oldStackNbt = oldStack.writeNbt(new NbtCompound());

            // Convert the old stack to NBT and store it in the new stack
            NbtCompound newStackNbt = newStack.getOrCreateNbt();
            newStackNbt.put(ROOT_ID, oldStackNbt);

            player.setStackInHand(Hand.MAIN_HAND, newStack);
        }
    }

    public static ItemVariant combine(ItemVariant variant1, ItemVariant variant2)
    {
        if (isTransformingTool(variant1) || isTransformingTool(variant2))
            return null;

        NbtCompound nbt1 = variant1.getNbt() != null ? variant1.copyNbt() : new NbtCompound();

        ItemStack itemStack2 = variant2.toStack();
        setLore(itemStack2.getOrCreateNbt(), Text.literal("[Transforming Tool]").formatted(Formatting.AQUA));
        setLore(nbt1, Text.literal("[Transforming Tool]").formatted(Formatting.AQUA));

        nbt1.put(ROOT_ID, itemStack2.writeNbt(new NbtCompound()));

        return ItemVariant.of(variant1.getItem(), nbt1);
    }

    protected static <T extends NbtElement> T getOrCreate(NbtCompound nbt, String key, int type, Supplier<T> supplier)
    {
        NbtElement element = nbt.get(key);
        if (element == null || element.getType() != type)
        {
            element = supplier.get();
            nbt.put(key, element);
        }
        return (T) element;
    }

    public static NbtCompound getOrCreateSubNbt(NbtCompound root, String key)
    {
        if (root == null || !root.contains(key, 10))
        {
            NbtCompound nbtCompound = new NbtCompound();
            root.put(key, nbtCompound);
            return nbtCompound;
        }
        return root.getCompound(key);
    }

    protected static NbtCompound setLore(NbtCompound nbt, Text lore)
    {
        NbtCompound displayCompound = getOrCreateSubNbt(nbt, ItemStack.DISPLAY_KEY);
        NbtList loreList = getOrCreate(displayCompound, "Lore", NbtType.LIST, NbtList::new);
        if (lore != null)
        {
            loreList.add(NbtString.of(Text.Serializer.toJson(lore)));
        }
        else
        {
            displayCompound.remove(ItemStack.LORE_KEY);
        }
        nbt.put(ItemStack.DISPLAY_KEY, displayCompound);
        return nbt;
    }
}