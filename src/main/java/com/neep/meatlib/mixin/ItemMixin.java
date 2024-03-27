package com.neep.meatlib.mixin;

import com.neep.meatlib.client.api.event.AppendTooltipEvent;
import com.neep.meatlib.item.MeatlibItemExtension;
import com.neep.meatlib.item.MeatlibItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin implements MeatlibItemExtension
{
    @Shadow public abstract boolean isFood();

    @Unique
    private ItemGroup itemGroup;

    @Unique
    private boolean supportsGuideLookup;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onInit(Item.Settings settings, CallbackInfo ci)
    {
        if (settings instanceof MeatlibItemSettings meatlibItemSettings)
        {
            this.itemGroup = meatlibItemSettings.group;
            this.supportsGuideLookup = meatlibItemSettings.supportsGuideLookup;
        }
    }

//    @Inject(at = @At("TAIL"), method = "appendTooltip*")
//    private void onAppendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci)
//    {
//    }

    @Override
    public @Nullable ItemGroup meatlib$getItemGroup()
    {
        return itemGroup;
    }

    @Override
    public boolean meatlib$supportsGuideLookup()
    {
        return supportsGuideLookup;
    }
}
