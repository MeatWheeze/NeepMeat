package com.neep.neepmeat.machine.charnel_pump;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.LazySupplier;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

import java.util.Set;

public class CharnelPumpBlockEntity extends SyncableBlockEntity implements LivingMachineComponent
{
    private final Random random = Random.create();

    private final LazySupplier<BlockEntityFinder<WellHeadBlockEntity>> wellHeadFinder = LazySupplier.of(() ->
        new BlockEntityFinder<>(getWorld(), NMBlockEntities.WELL_HEAD, 20).addAll(BlockEntityFinder.chunkRange(getPos())));

    private final LazySupplier<BlockEntityFinder<WrithingEarthSpoutBlockEntity>> writhingSpoutFinder = LazySupplier.of(() ->
        new BlockEntityFinder<>(getWorld(), NMBlockEntities.WRITHING_EARTH_SPOUT, 20).addAll(BlockEntityFinder.chunkRange(getPos())));

    public final long minPower = 1000;
    private float lastPower;
    private float inputPower;

    public CharnelPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick(Storage<FluidVariant> inputStorage)
    {
        wellHeadFinder.get().tick();
        writhingSpoutFinder.get().tick();

        if (inputPower != lastPower)
        {
            lastPower = inputPower;
            sync();
        }

        Set<WellHeadBlockEntity> found = wellHeadFinder.get().result();
        long distributeAmount = FluidConstants.BUCKET; // Integer multiple of bucket, will vary based on power input.

        if (inputPower >= (float) minPower / PowerUtils.referencePower())
        {
            spawnSpouts();

            for (var wellHead : found)
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    long extracted = inputStorage.extract(FluidVariant.of(NMFluids.STILL_WORK_FLUID), distributeAmount, transaction);
                    if (extracted == distributeAmount)
                    {
                        wellHead.receiveFluid(distributeAmount, transaction);
                        transaction.commit();
                    }
                    else
                    {
                        transaction.abort();
                    }
                }
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

                ((ServerWorld) world).spawnParticles(NMParticles.BODY_COMPOUND_SHOWER, surfacePos.getX() + 0.5, surfacePos.getY() + 1, surfacePos.getZ() + 0.5,
                        50,
                        1, 10, 1, 0.1);
            }
        }
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
}
