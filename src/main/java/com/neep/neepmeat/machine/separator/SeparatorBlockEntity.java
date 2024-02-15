package com.neep.neepmeat.machine.separator;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class SeparatorBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
    private float power;
    private int remainder = 4;
    private boolean takeBabies = false;

    public SeparatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public boolean motorTick(MotorEntity motor)
    {
        int waitTime = (int) (20 + (1 - Math.min(power, 1000) / 1000) * 100);
        if (power > 0 && world.getTime() % waitTime == 0)
        {
            int radius = 2;
            BlockPos centre = pos.offset(getCachedState().get(SeparatorBlock.FACING), radius + 1);
            Box box = new Box(centre);
            box = box.expand(radius, 2, radius);

            var entities = world.getEntitiesByType(TypeFilter.instanceOf(AnimalEntity.class), box, this::validEntity);
            entities.stream()
                    .skip(remainder)
                    .forEach(this::processEntity);
        }

        return false;
    }

    private boolean validEntity(AnimalEntity entity)
    {
        return takeBabies == entity.isBaby();
    }

    private void processEntity(AnimalEntity entity)
    {
        Vec3d outputPos = Vec3d.ofCenter(pos.offset(getCachedState().get(SeparatorBlock.FACING).getOpposite()));
        entity.setPosition(outputPos);
    }

    @Override
    public void setInputPower(float power)
    {
        this.power = power;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("power", power);
        nbt.putInt("remainder", remainder);
        nbt.putBoolean("take_babies", takeBabies);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.power = nbt.getFloat("power");
        this.remainder = nbt.getInt("remainder");
        this.takeBabies = nbt.getBoolean("take_babies");
    }
}
