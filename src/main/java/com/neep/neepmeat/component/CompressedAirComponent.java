package com.neep.neepmeat.component;

import com.neep.neepmeat.init.NMComponents;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class CompressedAirComponent implements Component, PlayerComponent<CompressedAirComponent>, ServerTickingComponent, AutoSyncedComponent
{
    private final PlayerEntity player;
    private long air;
    private final long capacity = 20;

    public CompressedAirComponent(PlayerEntity player)
    {

        this.player = player;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag)
    {
        this.air = tag.getLong("air");
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag)
    {
        tag.putLong("air", air);
    }

    @Override
    public void serverTick()
    {
        long prevAir = air;
        air = Math.max(0, air - 1);
        if (prevAir != air)
            NMComponents.COMPRESSED_AIR.sync(player);
    }

    public void insertAir(int air)
    {
        this.air = Math.min(capacity, this.air + air);
        NMComponents.COMPRESSED_AIR.sync(player);
    }

    public long getAir()
    {
        return air;
    }
}
