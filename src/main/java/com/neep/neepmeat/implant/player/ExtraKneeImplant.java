package com.neep.neepmeat.implant.player;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ExtraKneeImplant implements EntityImplant
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "extra_knee");

    protected final PlayerEntity player;

    public ExtraKneeImplant(Entity player)
    {
        this.player = (PlayerEntity) player;
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
        if (player.getWorld().getTime() % 40 == 0)
        {
            if (player.getStepHeight() < 1.1f)
            {
                player.setStepHeight(1.1f);
            }
        }
    }

    @Override
    public void clientTick()
    {
        if (player.getWorld().getTime() % 40 == 0)
        {
            if (player.getStepHeight() < 1.1f)
            {
                player.setStepHeight(1.1f);
            }
        }
    }

    @Override
    public void onInstall()
    {
        player.setStepHeight(1f);
    }

    @Override
    public void onUninstall()
    {
        player.setStepHeight(0.6f);
    }
}
