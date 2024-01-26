package com.neep.neepmeat.implant.player;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

// Dr Crobar's Lung Extensions
public class LungExtensionsImplant implements EntityImplant
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "lung_extensions");
    protected final PlayerEntity player;

    public LungExtensionsImplant(Entity entity)
    {
        this.player = (PlayerEntity) entity;
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
