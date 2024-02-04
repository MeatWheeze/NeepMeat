package com.neep.neepmeat.plc.robot;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface PLCActuator
{
    void setTarget(@Nullable BlockPos target);

    boolean reachedTarget();

    void spawnItem(ResourceAmount<ItemVariant> stored);

    void dumpStored();

    void setStored(@Nullable ResourceAmount<ItemVariant> stored);

    BlockPos getBasePos();

    @Nullable
    ResourceAmount<ItemVariant> getStored();

    double getX();
    double getY();
    double getZ();
}
