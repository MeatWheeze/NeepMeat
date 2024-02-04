package com.neep.neepmeat.plc.arm;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.plc.robot.PLCActuator;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class RoboticArmBlockEntity extends SyncableBlockEntity implements PLCActuator
{
    private @Nullable BlockPos target;

    private double tipX = getPos().getX() + 1;
    private double tipY = getPos().getY() + 1;
    private double tipZ = getPos().getZ();

    private double prevX = tipX;
    private double prevY = tipY;
    private double prevZ = tipZ;

    private Vec3d targetVec = new Vec3d(tipX, tipY, tipZ);
    @Nullable
    private ResourceAmount<ItemVariant> stored;

    public RoboticArmBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putDouble("rx", tipX);
        nbt.putDouble("ry", tipY);
        nbt.putDouble("rz", tipZ);

        nbt.putDouble("tx", targetVec.x);
        nbt.putDouble("ty", targetVec.y);
        nbt.putDouble("tz", targetVec.z);

        if (target != null)
            nbt.put("target", NbtHelper.fromBlockPos(target));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.tipX = nbt.getDouble("rx");
        this.tipY = nbt.getDouble("ry");
        this.tipZ = nbt.getDouble("rz");

        this.targetVec = new Vec3d(
                nbt.getDouble("tx"),
                nbt.getDouble("ty"),
                nbt.getDouble("tz")
        );

        if (nbt.contains("target"))
            this.target = NbtHelper.toBlockPos(nbt.getCompound("target"));
    }

    public void serverTick()
    {
        prevX = tipX;
        prevY = tipY;
        prevZ = tipZ;

        moveTo(targetVec);
    }

    public void clientTick()
    {
        prevX = tipX;
        prevY = tipY;
        prevZ = tipZ;

        moveTo(targetVec);
    }

    private void moveTo(Vec3d toPos)
    {
        if (!reachedTarget())
        {
            double dx = (toPos.x - tipX);
            double dy = (toPos.y - tipY);
            double dz = (toPos.z - tipZ);
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double vx = dx;
            double vy = dy;
            double vz = dz;

            // Normalise motion vector if distance is greater than base movement step
            if (dist > getSpeed())
            {
                vx = vx / dist * getSpeed();
                vy = vy / dist * getSpeed();
                vz = vz / dist * getSpeed();
            }

            // Step position
            tipX += vx;
            tipY += vy;
            tipZ += vz;
        }
    }

    private double getSpeed()
    {
        return 0.1;
    }

    @Override
    public void setTarget(@Nullable BlockPos target)
    {
        this.target = target;
        if (target != null)
            this.targetVec = Vec3d.ofCenter(target, 0.7);
        else
            this.targetVec = new Vec3d(tipX, tipY, tipZ);

        sync();
    }

    @Override
    public boolean reachedTarget()
    {
        return target == null || targetVec.squaredDistanceTo(tipX, tipY,tipZ) <= 0.1 * 0.1;
    }

    @Override
    public void spawnItem(ResourceAmount<ItemVariant> stored)
    {
        if (stored == null)
            return;
        ItemScatterer.spawn(getWorld(), tipX, tipY, tipZ, stored.resource().toStack((int) stored.amount()));
    }

    @Override
    public void dumpStored()
    {
        if (stored != null)
        {
            spawnItem(stored);
            stored = null;
        }
    }

    @Override
    public void setStored(@Nullable ResourceAmount<ItemVariant> stored)
    {
        this.stored = stored;
    }

    @Override
    public @Nullable ResourceAmount<ItemVariant> getStored()
    {
        return stored;
    }

    @Override
    public double getX() { return tipX; }

    @Override
    public double getY() { return tipY; }

    @Override
    public double getZ() { return tipZ; }

    public Vec3d getTarget(float tickDelta)
    {
        return new Vec3d(
                MathHelper.lerp(tickDelta, prevX, tipX),
                MathHelper.lerp(tickDelta, prevY, tipY),
                MathHelper.lerp(tickDelta, prevZ, tipZ)
        );
    }
}
