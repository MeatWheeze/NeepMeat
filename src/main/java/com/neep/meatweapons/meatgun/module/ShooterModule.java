package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;

public abstract class ShooterModule extends AbstractMeatgunModule implements AmmunitionRequiringModule, AmmunitionStoringModule
{
    private final int amountPerShot;
    protected final int maxCooldown;

    protected int ammoAmount;
    private final AmmunitionType type;
    protected int cooldown;

    public ShooterModule(RootModuleHolder.Listener listener, int amountPerShot, int maxCooldown, AmmunitionType type)
    {
        super(listener);
        this.amountPerShot = amountPerShot;
        this.maxCooldown = maxCooldown;

        this.ammoAmount = 0;
        this.type = type;
        this.cooldown = 0;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("stored", ammoAmount);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.ammoAmount = nbt.getInt("stored");
    }

    @Override
    public AmmunitionType ammoType()
    {
        return type;
    }

    @Override
    public boolean consume(int amount, Inventory inventory, PlayerEntity player)
    {
        return listener.getHolder().getAmmoOrReload(this, amount, inventory, player);
    }

    protected boolean consume(Inventory inventory, PlayerEntity player)
    {
        cooldown = 10; // Minimal cooldown for reloading
        return consume(amountPerShot, inventory, player);
    }

    @Override
    public int capacity()
    {
        return 16;
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

    //    @Override
//    public boolean reloadFrom(AmmunitionProvider provider, PlayerEntity player)
//    {
//        if (ammoType() != provider.ammoType())
//            return false;
//
//        int available = provider.getAmount();
//
//        stored = available; // TODO: eject old item
//        provider.consume();
//        player.playSound(NMSounds.RELOAD, SoundCategory.PLAYERS, 1, 1);
//        MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.DOWN, 30, 1.0f, 30 / 10f, 0.1f);
//        return true;
//    }
}
