package com.neep.meatweapons.item.meatgun;

import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatweapons.meatgun.module.BaseStaffModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

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
}
