package com.neep.neepmeat.machine.pylon;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.client.sound.PylonSoundInstance;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PylonBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    private boolean hasSoundInstance = false;
    public float angle;
    private float speed;

    public PylonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public PylonBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.PYLON, pos, state);
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);
        if (world.isClient() && !hasSoundInstance)
        {
            Client.spawnSound(this, pos);
            this.hasSoundInstance = true;
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, PylonBlockEntity be)
    {
//        if (!be.hasSoundInstance)
//        {
//            BlockSoundPacket.send((ServerWorld) world, pos);
//            be.hasSoundInstance = true;
//        }
    }

    @Override
    public boolean tick(IMotorBlockEntity motor)
    {
        float prevSpeed = speed;
        this.speed = motor.getSpeed();
        if (prevSpeed != speed) sync();

        return false;
    }

    @Override
    public void setWorkMultiplier(float multiplier)
    {

    }

    public float getSpeed()
    {
        return speed;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("speed", speed);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.speed = nbt.getFloat("speed");
    }

    @Environment(value=EnvType.CLIENT)
    private static class Client
    {
        public static void spawnSound(PylonBlockEntity be, BlockPos pos)
        {
            MinecraftClient.getInstance().getSoundManager().play(new PylonSoundInstance(be, pos, NMSounds.PYLON_START, NMSounds.AIRTRUCK_RUNNING, SoundCategory.BLOCKS));
        }
    }
}
