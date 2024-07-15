package com.neep.meatweapons.item.meatgun;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.client.meatgun.animation.ChopMeatgunAnimation;
import com.neep.meatweapons.client.meatgun.animation.StaffIdleMeatgunAnimation;
import com.neep.meatweapons.client.meatgun.animation.HalberdChargeAnimation;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.BaseStaffModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.function.Supplier;

public class MeatgunStaffItem extends MeatgunItem
{
    public MeatgunStaffItem(String registryName, TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(registryName, tooltipSupplier, settings);
    }

    @Override
    public ArmPose getThirdPersonArmPose(AbstractClientPlayerEntity player, ItemStack stackInHand, Hand hand)
    {
        return ArmPose.ITEM;
    }

    @Override
    public MeatgunModule createBase(MeatgunComponent.Listener listener)
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
                        .add("blade_swing_down", new ChopMeatgunAnimation())
                        .add("upper_thrust", new HalberdChargeAnimation())
                        ;
            }
        };
    }
}
