package com.neep.neepmeat.plc.robot;

import com.neep.neepmeat.api.plc.PLC;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public interface PLCActuator
{
    BlockPos getBasePos();

    void spawnItem(@Nullable ResourceAmount<ItemVariant> stored);

    @Nullable
    default ResourceAmount<ItemVariant> getStored(PLC plc)
    {
        error(plc);
        return null;
    }

    default void setStored(PLC plc, @Nullable ResourceAmount<ItemVariant> stored)
    {
        error(plc);
    }

    default void dumpStored(PLC plc)
    {
        spawnItem(getStored(plc));
    }

    default boolean reachedTarget(PLC plc)
    {
        error(plc);
        return true;
    }

    default void setTarget(PLC plc, @Nullable BlockPos target)
    {
        error(plc);
    }

    private void error(PLC plc)
    {
        if (plc != null)
            plc.raiseError(new PLC.Error("Actuator does not support action"));
    }

    default double getX() { return 0; }
    default double getY() { return 0; }
    default double getZ() { return 0; }

    PLCActuator EMPTY  = new PLCRobot()
    {
        @Override
        public void setTarget(PLC plc, @Nullable BlockPos target)
        {

        }

        @Override
        public boolean reachedTarget(PLC plc)
        {
            return false;
        }

        @Override
        public void spawnItem(@Nullable ResourceAmount<ItemVariant> stored)
        {

        }

        @Override
        public void dumpStored(PLC plc)
        {

        }

        @Override
        public void setStored(PLC plc, @Nullable ResourceAmount<ItemVariant> stored)
        {

        }

        @Override
        public BlockPos getBasePos()
        {
            return BlockPos.ORIGIN;
        }

        @Override
        public @Nullable ResourceAmount<ItemVariant> getStored(PLC plc)
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

        @Override
        public boolean actuatorRemoved()
        {
            return true;
        }
    };

    boolean actuatorRemoved();

    default EnumSet<Capability> capabilities()
    {
        return EnumSet.noneOf(Capability.class);
    }

    interface Provider
    {
        PLCActuator getPlcActuator();
    }

    enum Capability
    {
        MOVE_ROBOT,
        ROUTE_ITEM,
    }
}
