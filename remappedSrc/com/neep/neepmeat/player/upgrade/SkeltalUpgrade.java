package com.neep.neepmeat.player.upgrade;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.util.Identifier;

public class SkeltalUpgrade implements PlayerUpgrade
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "skeltal");

    protected final PlayerEntity player;

    public SkeltalUpgrade(PlayerEntity player)
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
        if (source.isIn(DamageTypeTags.IS_FALL))
        {
            return 20f;
        }

        return 0;
    }
}
