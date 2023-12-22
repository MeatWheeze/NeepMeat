package com.neep.neepmeat.player.upgrade;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ExtraKneeUpgrade implements PlayerUpgrade
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "extra_knee");

    protected final PlayerEntity player;

    public ExtraKneeUpgrade(PlayerEntity player)
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
        if (player.world.getTime() % 10 == 0) player.setStepHeight(1f);
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
