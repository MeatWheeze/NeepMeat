package com.neep.meatweapons.item.meatgun;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.client.meatgun.animation.*;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.meatgun.module.BaseStaffModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.function.Supplier;

public class MeatgunStaffItem extends MeatgunItem
{
    public MeatgunStaffItem(TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(tooltipSupplier, settings);
    }

    @Override
    public ArmPose getThirdPersonArmPose(AbstractClientPlayerEntity player, ItemStack stackInHand, Hand hand)
    {
        return ArmPose.ITEM;
    }

    @Override
    public MeatgunModule createBase(RootModuleHolder.Listener listener)
    {
        return new BaseStaffModule(listener);
    }

    @Override
    public Supplier<Object> createAnimationManager()
    {
        return new Supplier<Object>()
        {
            @Override
            public Object get()
            {
                return new MeatgunAnimationManager(new StaffIdleMeatgunAnimation())
                        .add("reload", new StaffReloadMeatgunAnimation())
                        .add("blade_swing_down", new ChopMeatgunAnimation())
                        .add("upper_thrust", new HalberdChargeAnimation())
                        .add("hook_grab", new HalberdGrabAnimation())
                        .add("swing_across", new SwingAcrossMeatgunAnimation())
                        ;
            }
        };
    }

    @Override
    public int getMaxComplexity(ItemStack stack)
    {
        return 16;
    }
}
