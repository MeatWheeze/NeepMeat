package com.neep.neepmeat.machine.pylon;

import com.google.common.collect.MapMaker;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.DataPort;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.client.hud.HUDOverlays;
import com.neep.neepmeat.client.sound.PylonSoundInstance;
import com.neep.neepmeat.entity.GlomeEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.advanced_integrator.SimpleDataPort;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

public class PylonBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
    public static final float RUNNING_SPEED = 16;

    protected final int radius = 7;
    public float angle;
    private float speed;

    private SimpleDataPort port = new SimpleDataPort(this);

    protected final Random random = new Random(0);

    public PylonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public PylonBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.PYLON, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, PylonBlockEntity be)
    {
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

        Client client = Client.MAP.computeIfAbsent(this, pylon -> new Client(this, pos));
        client.tick();
    }

    @Override
    public boolean tick(MotorEntity motor)
    {
        float prevSpeed = speed;
        this.speed = motor.getSpeed();
        if (prevSpeed != speed) sync();

        if (isRunning())
        {
            spawnGlomes();

            try (Transaction transaction = Transaction.openOuter())
            {
//                long sendAmount =
                port.send(DataVariant.NORMAL, 1, transaction);
                transaction.commit();
            }
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
        port.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.speed = nbt.getFloat("speed");
        port.readNbt(nbt);
    }

    public DataPort getPort(Void unused)
    {
        return port;
    }

    @Environment(value=EnvType.CLIENT)
    private static class Client
    {
        static Map<PylonBlockEntity, Client> MAP = new MapMaker().weakKeys().makeMap();

        private final PylonBlockEntity be;
        private final BlockPos pos;
        private final MinecraftClient client = MinecraftClient.getInstance();
        private PylonSoundInstance mainSound;
        private PylonSoundInstance runningSound;

        Client(PylonBlockEntity be, BlockPos pos)
        {
            this.be = be;
            this.pos = pos;
            this.mainSound = new PylonSoundInstance(be, pos, NMSounds.PYLON_START, SoundCategory.BLOCKS);
            this.runningSound = new PylonSoundInstance(be, pos, NMSounds.PYLON_ACTIVE, SoundCategory.BLOCKS);
        }

        protected static void causeVignette()
        {
            HUDOverlays.startPylonVignette();
        }

        public void tick()
        {
            if (!client.getSoundManager().isPlaying(mainSound))
            {
                client.getSoundManager().play(mainSound);
            }
            if (be.isRunning() && !client.getSoundManager().isPlaying(runningSound))
            {
                client.getSoundManager().play(runningSound);
            }
            if (!be.isRunning() && client.getSoundManager().isPlaying(runningSound))
            {
                client.getSoundManager().stop(runningSound);
            }
        }
    }
}
