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

    PLCActuator EMPTY  = new PLCActuator()
    {
        @Override
        public void setTarget(@Nullable BlockPos target)
        {

        }

        @Override
        public boolean reachedTarget()
        {
            return false;
        }

        @Override
        public void spawnItem(ResourceAmount<ItemVariant> stored)
        {

        }

        @Override
        public void dumpStored()
        {

        }

        @Override
        public void setStored(@Nullable ResourceAmount<ItemVariant> stored)
        {

        }

        @Override
        public BlockPos getBasePos()
        {
            return BlockPos.ORIGIN;
        }

        @Override
        public @Nullable ResourceAmount<ItemVariant> getStored()
        {
            return null;
        }

        @Override
        public double getX()
        {
            return 0;
        }

        @Override
        public double getY()
        {
            return 0;
        }

        @Override
        public double getZ()
        {
            return 0;
        }
    };
}
