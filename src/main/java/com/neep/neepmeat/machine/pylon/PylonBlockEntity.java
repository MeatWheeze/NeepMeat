package com.neep.neepmeat.machine.pylon;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.client.sound.PylonSoundInstance;
import com.neep.neepmeat.entity.GlomeEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Random;

public class PylonBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    public static final float RUNNING_SPEED = 16;

    private boolean hasSoundInstance = false;
    public float angle;
    private float speed;

    protected final Random random = new Random(0);

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

    public boolean isRunning()
    {
        return getSpeed() >= RUNNING_SPEED;
    }

    @Override
    public boolean tick(IMotorBlockEntity motor)
    {
        float prevSpeed = speed;
        this.speed = motor.getSpeed();
        if (prevSpeed != speed) sync();

        if (isRunning())
        {
            spawnGlomes();
        }

        return false;
    }

    private void spawnGlomes()
    {
        int radius = 7;
        BlockPos volumeStart = getPos().subtract(new Vec3i(radius, radius, radius));
        int end = 2 * radius + 1;
        if (random.nextFloat() < 0.05)
        {
            BlockPos entityBlockPos = volumeStart.add(random.nextInt(end), random.nextInt(end), random.nextInt(end));
            Vec3d entityPos = Vec3d.ofCenter(entityBlockPos);
            if (world.isAir(entityBlockPos))
            {
                GlomeEntity entity = new GlomeEntity(world, entityPos.x, entityPos.y - 0.2, entityPos.z, 0, 0, 0);
                world.spawnEntity(entity);
            }
        }
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
