package com.neep.neepmeat.implant.player;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;

// Thank Mr Skeltal for calcium and good bones
public class SkeltalImplant implements EntityImplant
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "skeltal");

    protected final PlayerEntity player;

    public SkeltalImplant(PlayerEntity player)
    {
        this.player = player;
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

    @Override
    public float getProtectionAmount(DamageSource source, float amount)
    {
//        if (source.isIn(DamageTypeTags.IS_FALL))
        if (source.isIn(DamageTypeTags.IS_FALL))
        {
            return 20f;
        }

        return 0;
    }
}
