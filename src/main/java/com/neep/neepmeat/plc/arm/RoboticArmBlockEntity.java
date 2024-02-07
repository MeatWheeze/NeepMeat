package com.neep.neepmeat.plc.arm;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.neepasm.compiler.variable.EmptyVariableStack;
import com.neep.neepmeat.neepasm.compiler.variable.Variable;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.robot.PLCActuator;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.Stack;
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

import java.util.function.Consumer;

public class RoboticArmBlockEntity extends SyncableBlockEntity implements PLCActuator, PLCActuator.Provider, MotorisedBlock
{
    private float power;

    private @Nullable BlockPos target;

    private double tipX = getPos().getX() + 1;
    private double tipY = getPos().getY() + 1;
    private double tipZ = getPos().getZ();

    public double prevX = tipX;
    public double prevY = tipY;
    public double prevZ = tipZ;

    private final double range = 4.5;

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

        nbt.putFloat("power", power);

        nbt.put("stored", Instruction.writeItem(stored));

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

        this.power = nbt.getFloat("power");

        this.targetVec = new Vec3d(
                nbt.getDouble("tx"),
                nbt.getDouble("ty"),
                nbt.getDouble("tz")
        );

        this.stored = Instruction.readItem(nbt.getCompound("stored"));

        if (nbt.contains("target"))
            this.target = NbtHelper.toBlockPos(nbt.getCompound("target"));
    }

    public void serverTick()
    {
        prevX = tipX;
        prevY = tipY;
        prevZ = tipZ;

//        if (targetVec != null)
//        {
//            double distance = targetVec.distanceTo(new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ()));
//            if (distance < range)
//        }

        moveTo(targetVec);
    }

    public void clientTick()
    {
//        if (targetVec != null)
//        {
//            double distance = targetVec.distanceTo(new Vec3d(pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ()));
//            if (distance < range)
//        }
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

            if (origin().squaredDistanceTo(tipX + vx, tipY + vy, tipZ + vz) >= range * range)
                return;

            // Step position
            tipX += vx;
            tipY += vy;
            tipZ += vz;
        }
    }

    private Vec3d origin()
    {
        return new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
    }

    private double getSpeed()
    {
        return MathHelper.clamp(power, 0, 1.5);
    }

    @Override
    public void setTarget(@Nullable BlockPos target)
    {
        this.target = target;
        if (target != null)
            this.targetVec = Vec3d.ofCenter(target, 0.0);
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
            markDirty();
        }
    }

    @Override
    public void setStored(@Nullable ResourceAmount<ItemVariant> stored)
    {
        this.stored = stored;
        markDirty();
    }

    @Override
    public BlockPos getBasePos()
    {
        return pos;
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

//    public Vec3d getTarget(float tickDelta)
//    {
//        return new Vec3d(
//                MathHelper.lerp(tickDelta, prevX, tipX),
//                MathHelper.lerp(tickDelta, prevY, tipY),
//                MathHelper.lerp(tickDelta, prevZ, tipZ)
//        );
//    }

    @Override
    public boolean tick(MotorEntity motor)
    {
        return false;
    }

    @Override
    public void setInputPower(float power)
    {
        this.power = power;
        sync();
    }

    @Override
    public PLCActuator get()
    {
        return this;
    }
}
