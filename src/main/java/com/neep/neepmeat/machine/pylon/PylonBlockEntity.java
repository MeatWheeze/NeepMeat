package com.neep.neepmeat.machine.pylon;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.LazySupplier;
import com.neep.neepmeat.api.DataPort;
import com.neep.neepmeat.api.DataVariant;
import com.neep.neepmeat.api.enlightenment.EnlightenmentUtil;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.client.hud.HUDOverlays;
import com.neep.neepmeat.client.sound.PylonSoundInstance;
import com.neep.neepmeat.entity.GlomeEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.machine.advanced_integrator.AdvancedIntegratorBlockEntity;
import com.neep.neepmeat.machine.advanced_integrator.SimpleDataPort;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.machine.well_head.BlockEntityFinder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.neep.neepmeat.machine.well_head.BlockEntityFinder.chunkRange;

public class PylonBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
    public static final float RUNNING_SPEED = 16;
    public static final float MAX_SPEED = 40;

    protected final int radius = 7; // Enlightenment and glome radius
    public float angle;
    private float speed;
    private float level;

    private final SimpleDataPort port = new SimpleDataPort(this);

    private final LazySupplier<BlockEntityFinder<AdvancedIntegratorBlockEntity>> integratorFinder = LazySupplier.of(() ->
            new BlockEntityFinder<>(getWorld(), NMBlockEntities.ADVANCED_INTEGRATOR, 40)
                    .addAll(chunkRange(getPos()))
                    .predicate(be -> be.getPos().isWithinDistance(getPos(), radius * 2)));

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

    @Override
    public boolean tick(MotorEntity motor)
    {
        integratorFinder.get().tick();

        float prevSpeed = speed;
        this.speed = motor.getSpeed();
        if (prevSpeed != speed)
            sync();

        if (isRunning())
        {
            if (world.getTime() % 80 == 0)
            {
                level = updateLevel(world, pos.getX(), pos.getY(), pos.getZ());
            }

            spawnGlomes();

            try (Transaction transaction = Transaction.openOuter())
            {
                float f = effectMultiplier();
                integratorFinder.get().result().forEach(i ->
                {
                    if (random.nextBoolean())
                        i.getDataStorage().insert(DataVariant.NORMAL, (long) (1 + effectMultiplier() * 20), transaction);
                });
                transaction.commit();

                int enlightenment = (int) (effectMultiplier() * 10);
                Vec3d centre = Vec3d.ofCenter(pos);
                PlayerLookup.around((ServerWorld) world, centre, radius).forEach(p ->
                {
                    EnlightenmentUtil.applyDose(p, enlightenment, p.squaredDistanceTo(centre), 1 / 50.0);
                });
            }
        }

        return false;
    }

    private static float updateLevel(World world, int x, int y, int z)
    {
        int level = 0;

        for (int depth = 1; depth <= 4; depth++)
        {
            int j = y - depth;
            for (int i = x - depth; i <= x + depth; ++i)
            {
                for (int k = z - depth; k <= z + depth; ++k)
                {
                    if (world.getBlockState(new BlockPos(i, j, k)).isOf(NMBlocks.MEAT_STEEL_BLOCK))
                        level++;
                }
            }
        }

        return level / 164f;
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
        if (random.nextFloat() < 0.05 * (1 - effectMultiplier()))
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

    private float effectMultiplier()
    {
        return MathHelper.clamp(speed / MAX_SPEED, 0, 1) * level;
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


    // --- Client things ---

    @Environment(value=EnvType.CLIENT)
    @Nullable
    private Client client;

    @Environment(EnvType.CLIENT)
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

        if (client == null)
            client = new Client(this, pos);
        client.tick();
    }

    @Environment(value=EnvType.CLIENT)
    private static class Client
    {
        private final PylonBlockEntity be;
        private final MinecraftClient client = MinecraftClient.getInstance();
        private final PylonSoundInstance mainSound;
        private final PylonSoundInstance runningSound;

        Client(PylonBlockEntity be, BlockPos pos)
        {
            this.be = be;
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
