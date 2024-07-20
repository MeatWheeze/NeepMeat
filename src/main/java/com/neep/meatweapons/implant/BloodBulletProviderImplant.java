package com.neep.meatweapons.implant;

import com.neep.meatweapons.MeatWeapons;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.implant.player.EntityImplant;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class BloodBulletProviderImplant implements EntityImplant
{
    public static final Identifier ID = new Identifier(MeatWeapons.NAMESPACE, "blood_bullet_provider");

    private final Entity entity;


    public BloodBulletProviderImplant(Entity entity)
    {

        this.entity = entity;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }

    @Override
    public void tick()
    {

    }
}
