package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.AmmunitionProvider;
import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;

public abstract class ShooterModule extends AbstractMeatgunModule implements AmmunitionRequiringModule
{
    protected final int capacity;
    private final int amountPerShot;
    protected final int maxCooldown;

    protected int stored;
    private final AmmunitionType type;
    protected int cooldown;

    public ShooterModule(RootModuleHolder.Listener listener, int maxShots, int amountPerShot, int maxCooldown, AmmunitionType type)
    {
        super(listener);
        this.capacity = maxShots * amountPerShot;
        this.amountPerShot = amountPerShot;
        this.maxCooldown = maxCooldown;

        this.stored = 0;
        this.type = type;
        this.cooldown = 0;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("stored", stored);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.stored = nbt.getInt("stored");
    }

    @Override
    public AmmunitionType ammoType()
    {
        return type;
    }

    @Override
    public int capacity()
    {
        return capacity;
    }

    @Override
    public boolean consume(int amount, Inventory inventory, PlayerEntity player)
    {
        if (stored >= amount)
        {
            stored -= amount;
            return true;
        }
        else
        {
            return listener.getHolder().getAmmoOrReload(this, amount, inventory, player);
        }
    }

    protected boolean consume(Inventory inventory, PlayerEntity player)
    {
        cooldown = 10; // Minimal cooldown for reloading
        return consume(amountPerShot, inventory, player);
    }

    @Override
    public boolean reloadFrom(AmmunitionProvider provider, PlayerEntity player)
    {
        int available = provider.getAmount();

        if (stored + available <= capacity)
        {
            stored += available;
            provider.consume();
            player.playSound(NMSounds.RELOAD, SoundCategory.PLAYERS, 1, 1);
            return true;
        }

        return false;
    }
}
