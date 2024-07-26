package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import net.minecraft.nbt.NbtCompound;

public class BatteryModule extends AbstractMeatgunModule implements AmmunitionStoringModule
{
    private int ammoAmount;

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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("ammo_amount", ammoAmount);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.ammoAmount = nbt.getInt("ammo_amount");
    }

    @Override
    public int capacity()
    {
        return 16;
    }

    @Override
    public AmmunitionType ammoType()
    {
        return AmmunitionType.ENERGY;
    }

    @Override
    public int amount()
    {
        return ammoAmount;
    }

    @Override
    public int insert(int maxAmount)
    {
        int inserted = Math.min(maxAmount, capacity() - ammoAmount);
        if (inserted > 0)
        {
            ammoAmount += inserted;
        }
        return inserted;
    }

    @Override
    public int extract(int maxAmount)
    {
        int extracted = Math.min(ammoAmount, maxAmount);
        if (extracted > 0)
        {
            ammoAmount -= extracted;
            return extracted;
        }
        return 0;
    }
}
