package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.ClientComponents;
import com.neep.meatlib.util.LazySupplier;
import com.neep.neepmeat.BalanceConstants;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.component.PoweredComponent;
import com.neep.neepmeat.machine.well_head.BlockEntityFinder;
import com.neep.neepmeat.machine.well_head.WellHeadBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Set;

public class CharnelPumpBlockEntity extends SyncableBlockEntity implements LivingMachineComponent, PoweredComponent
{
    private final Random random = Random.create();

    private final LazySupplier<BlockEntityFinder<WellHeadBlockEntity>> wellHeadFinder = LazySupplier.of(() ->
        new BlockEntityFinder<>(getWorld(), NMBlockEntities.WELL_HEAD, 20).addAll(BlockEntityFinder.chunkRange(getPos())));

    private final LazySupplier<BlockEntityFinder<WrithingEarthSpoutBlockEntity>> writhingSpoutFinder = LazySupplier.of(() ->
        new BlockEntityFinder<>(getWorld(), NMBlockEntities.WRITHING_EARTH_SPOUT, 20).addAll(BlockEntityFinder.chunkRange(getPos())));

    private final ClientComponents.Holder<?> holder = new ClientComponents.Holder<>(this);


    public int animationTicks;
    private float progressIncrement;

    public boolean hasAir;
    public boolean hasFluid;

    public CharnelPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public static boolean canRun(double puPower)
    {
        return puPower >= (double) BalanceConstants.CHARNEL_PUMP_MIN_POWER / PowerUtils.referencePower();
    }

    public void serverTick(double puPower, Storage<FluidVariant> inputStorage, Transaction transaction)
    {
        wellHeadFinder.get().tick();
        writhingSpoutFinder.get().tick();

        Set<WellHeadBlockEntity> found = wellHeadFinder.get().result();
        long distributeAmount = FluidConstants.BUCKET; // Integer multiple of bucket, will vary based on power input.

        // Consume compressed air
        boolean hasAir = false;
        try (Transaction inner = transaction.openNested())
        {
            long droplets = Math.max(10, (long) (puPower * BalanceConstants.CHARNEL_PUMP_POWER_TO_AIR));
            long extracted = inputStorage.extract(NMFluids.COMPRESSED_AIR.variant(), droplets, inner);
            if (extracted == droplets)
            {
                hasAir = true;
                inner.commit();
            }
        }

        if (this.hasAir != hasAir)
        {
            this.hasAir = hasAir;
            sync();
        }

        if (canRun(puPower))
        {
            spawnSpouts();

            // Consume work fluid and eject ores
            boolean fluidConsumed = false;
            if (hasAir)
            {
                for (var wellHead : found)
                {
                    try (Transaction inner = transaction.openNested())
                    {
                        long extracted = inputStorage.extract(FluidVariant.of(NMFluids.STILL_WORK_FLUID), distributeAmount, inner);
                        if (extracted == distributeAmount)
                        {
                            wellHead.receiveFluid(distributeAmount, inner);
                            fluidConsumed = true;
                            inner.commit();
                        }
                        else
                        {
                            inner.abort();
                        }
                    }
                }
            }

            if (this.hasFluid != fluidConsumed)
            {
                this.hasFluid = fluidConsumed;
                sync();
            }
        }
    }

    private boolean canSpoutSpawn(BlockPos surfacePos)
    {
        BlockState surfaceState = world.getBlockState(surfacePos);
        return BlockEntityFinder.chunkRange(pos).contains(world.getChunk(surfacePos).getPos())
                && (surfaceState.isIn(BlockTags.DIRT) || surfaceState.isIn(BlockTags.STONE_ORE_REPLACEABLES));
    }

    // Check for existing sprouts. If none are found, spawn a new one.
    private void spawnSpouts()
    {
        if (!world.getBlockState(pos.down()).isOf(NMBlocks.WRITHING_STONE))
            world.setBlockState(pos.down(), NMBlocks.WRITHING_STONE.getDefaultState(), Block.NOTIFY_ALL);

        var writhing = writhingSpoutFinder.get();

        if (writhing.notDirty() && writhing.result().isEmpty())
        {
            // Find a position on the surface within the adjacent 3x3 square of chunks.
            BlockPos rand = BlockPos.iterateRandomly(random, 1, getPos(), 25).iterator().next();
            int surfaceHeight = world.getChunk(rand).sampleHeightmap(Heightmap.Type.WORLD_SURFACE, rand.getX(), rand.getZ());
            BlockPos surfacePos = new BlockPos(rand.getX(), surfaceHeight, rand.getZ());

            if (canSpoutSpawn(surfacePos))
            {
                world.setBlockState(surfacePos, NMBlocks.WRITHING_EARTH_SPOUT.getDefaultState(), Block.NOTIFY_ALL);
                world.playSound(null, surfacePos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1, 1);

                ((ServerWorld) world).spawnParticles(NMParticles.BODY_COMPOUND_SHOWER, surfacePos.getX() + 0.5, surfacePos.getY() + 1, surfacePos.getZ() + 0.5,
                        50,
                        1, 10, 1, 0.1);
            }
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        nbt.putFloat("power", progressIncrement);
        nbt.putBoolean("has_air", hasAir);
        nbt.putBoolean("has_fluid", hasFluid);
        return nbt;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.progressIncrement = nbt.getFloat("power");
        this.hasAir = nbt.getBoolean("has_air");
        this.hasFluid = nbt.getBoolean("has_fluid");
    }

    @Override
    public void setController(BlockPos pos)
    {

    }

    @Override
    public boolean componentRemoved()
    {
        return isRemoved();
    }

    @Override
    public ComponentType<? extends LivingMachineComponent> getComponentType()
    {
        return LivingMachineComponents.CHARNEL_PUMP;
    }

    private void spawnAirParticles()
    {
        if (world.getTime() % 2 == 0)
        {
            double x = getPos().getX() + 0.5;
            double y = getPos().getY();
            double z = getPos().getZ() + 0.5;

            world.addParticle(ParticleTypes.POOF, x + 1, y + 1, z + 1, 0.1, 0.1, 0.1);
            world.addParticle(ParticleTypes.POOF, x - 1, y + 1, z + 1, -0.1, 0.1, 0.1);
            world.addParticle(ParticleTypes.POOF, x + 1, y + 1, z - 1, 0.1, 0.1, -0.1);
            world.addParticle(ParticleTypes.POOF, x - 1, y + 1, z - 1, -0.1, 0.1, -0.1);
        }
    }

    private void clientTickAir()
    {
        if (hasAir)
        {
            if (!canRun(progressIncrement))
            {
                spawnAirParticles();
            }
            else if (animationTicks < 30)
            {
                spawnAirParticles();
            }
        }

    }

    public static void clientTick(World world, BlockPos pos, BlockState state, CharnelPumpBlockEntity be)
    {
        be.holder.get().clientTick();
        be.clientTickAir();

        if (be.progressIncrement > 0 && be.hasAir)
        {
            be.animationTicks = Math.max(0, be.animationTicks - 1);
            if (be.animationTicks == 0)
            {
                be.animationTicks = 100;
            }
        }
        else
        {
            be.animationTicks = Math.max(0, be.animationTicks - 1);
        }
    }

    @Override
    public float progressIncrement()
    {
        return progressIncrement;
    }

    @Override
    public void setProgressIncrement(float progressIncrement)
    {
        if (progressIncrement != this.progressIncrement)
        {
            this.progressIncrement = progressIncrement;
            sync();
        }
    }

    public static boolean isRising(float animationTicks)
    {
        return animationTicks < 60;
    }
}
