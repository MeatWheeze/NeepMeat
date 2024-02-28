package com.neep.meatlib.mixin;

import com.neep.meatlib.item.MeatlibItemExtension;
import com.neep.meatlib.item.MeatlibItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class ItemMixin implements MeatlibItemExtension
{
    @Shadow public abstract boolean isFood();

    @Unique
    private ItemGroup itemGroup;

    @Inject(method = "<init>", at = @At("TAIL"))
    void onInit(Item.Settings settings, CallbackInfo ci)
    {
        if (settings instanceof MeatlibItemSettings meatlibItemSettings)
        {
            this.itemGroup = meatlibItemSettings.group;
        }
    }

    @Override
    public @Nullable ItemGroup meatlib$getItemGroup()
    {
        return itemGroup;
    }
}
