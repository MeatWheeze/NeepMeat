package com.neep.meatlib.mixin;

import com.neep.meatlib.attachment.itemstack.MeatItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin implements MeatItemStack
{
    private final NbtCompound volatileNbt = new NbtCompound();

    @Override
    public NbtCompound getVolatileNbt()
    {
        return volatileNbt;
    }
}
