package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import net.minecraft.nbt.NbtCompound;

public abstract class ShooterModule extends AbstractMeatgunModule
{
    protected final int maxShots;
    protected final int maxCooldown;

    protected int shotsRemaining;
    protected int cooldown;

    public ShooterModule(MeatgunComponent.Listener listener, int maxShots, int maxCooldown)
    {
        super(listener);
        this.maxShots = maxShots;
        this.maxCooldown = maxCooldown;

        this.shotsRemaining = maxShots;
        this.cooldown = 0;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("shots_remaining", shotsRemaining);
        nbt.putInt("cooldown", cooldown);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.cooldown = nbt.getInt("cooldown");
        this.shotsRemaining = nbt.getInt("shots_remaining");
    }
}
