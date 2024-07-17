package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.RootModuleHolder;
import net.minecraft.nbt.NbtCompound;

public class HomingModule extends AbstractMeatgunModule
{
    public HomingModule(RootModuleHolder.Listener listener)
    {
        super(listener);
    }

    public HomingModule(RootModuleHolder.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.HOMING_BRAIN;
    }
}
