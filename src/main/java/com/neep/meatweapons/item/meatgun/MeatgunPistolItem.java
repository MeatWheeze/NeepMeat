package com.neep.meatweapons.item.meatgun;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.client.meatgun.animation.MeatgunAnimation;
import com.neep.meatweapons.client.meatgun.animation.PistolReloadMeatgunAnimation;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.meatgun.module.BasePistolModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class MeatgunPistolItem extends MeatgunItem
{
    public MeatgunPistolItem(TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(tooltipSupplier, settings);
    }

    @Override
    public MeatgunModule createBase(RootModuleHolder.Listener listener)
    {
        return new BasePistolModule(listener);
    }

    @Override
    public Supplier<Object> createAnimationManager()
    {
        return new Supplier<Object>()
        {
            @Override
            public Object get()
            {
                return new MeatgunAnimationManager(MeatgunAnimation.EMPTY)
                        .add("reload", new PistolReloadMeatgunAnimation());
            }
        };
    }

    @Override
    public int getMaxComplexity(ItemStack stack)
    {
        return 16;
    }
}
