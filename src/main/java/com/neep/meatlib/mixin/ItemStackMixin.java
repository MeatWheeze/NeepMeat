package com.neep.meatlib.mixin;

import com.neep.meatlib.attachment.itemstack.MeatItemStack;
import com.neep.meatlib.client.api.event.AppendTooltipEvent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements MeatItemStack
{
    // What was this even for?
//    private final NbtCompound volatileNbt = new NbtCompound();

//    @Override
//    public NbtCompound getVolatileNbt()
//    {
//        return volatileNbt;
//    }

    @Inject(at = @At("TAIL"), method = "getTooltip")
    private void onTooltip(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir)
    {
        ItemStack stack = (ItemStack) (Object) this;
        AppendTooltipEvent.EVENT.invoker().onAppendTooltip(stack, player != null ? player.getWorld() : null, context);
    }
}
