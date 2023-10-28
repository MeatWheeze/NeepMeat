package com.neep.neepmeat.machine.pylon;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.client.hud.HUDOverlays;
import com.neep.neepmeat.client.sound.PylonSoundInstance;
import com.neep.neepmeat.entity.GlomeEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.Random;

public class PylonBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    public static final float RUNNING_SPEED = 16;

    private boolean hasSoundInstance = false;
    protected final int radius = 7;
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
            // This somehow works on servers.
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

    public void clientTick()
    {
        float clamped = MathHelper.clamp(getSpeed() - RUNNING_SPEED, 0, RUNNING_SPEED * 2) / (RUNNING_SPEED * 2);
        float threshold = MathHelper.lerp(clamped, 0f, 0.6f);
        float p = random.nextFloat();
        if (p < threshold)
        {
            world.getNonSpectatingEntities(PlayerEntity.class, getBox()).stream().findFirst().ifPresent(pl ->
            {
                Client.causeVignette();
            });
        }
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

    protected Box getBox()
    {
        BlockPos volumeStart = getPos().subtract(new Vec3i(radius, radius, radius));
        int end = 2 * radius + 1;
        return new Box(volumeStart, volumeStart.add(end, end, end));
    }

    private void spawnGlomes()
    {
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
    public void setInputPower(float power)
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
        protected static void spawnSound(PylonBlockEntity be, BlockPos pos)
        {
            MinecraftClient.getInstance().getSoundManager().play(new PylonSoundInstance(be, pos, NMSounds.PYLON_START, NMSounds.AIRTRUCK_RUNNING, SoundCategory.BLOCKS));
        }

        protected static void causeVignette()
        {
            HUDOverlays.startPylonVignette();
        }
    }
}
