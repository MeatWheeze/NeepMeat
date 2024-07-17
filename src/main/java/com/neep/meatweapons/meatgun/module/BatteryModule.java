package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.RootModuleHolder;
import net.minecraft.nbt.NbtCompound;

public class BatteryModule extends AbstractMeatgunModule
{
    public BatteryModule(RootModuleHolder.Listener listener)
    {
        super(listener);
    }

    public BatteryModule(RootModuleHolder.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.BATTERY;
    }
}
