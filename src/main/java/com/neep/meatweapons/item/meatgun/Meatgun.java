package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public interface Meatgun
{
    MeatgunModule createBase(RootModuleHolder.Listener listener);

    /**
     * @return An anonymous instance of {@link Supplier<MeatgunAnimationManager>} so that it won't get loaded on the server.
     */
    Supplier<Object> createAnimationManager();

    int getMaxComplexity(ItemStack stack);
}
