package com.neep.neepmeat.machine.heater;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.machine.Heatable;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.blood_network.BloodNetwork;
import com.neep.neepmeat.transport.blood_network.BloodTransferChangeListener;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class HeaterBlockEntity extends SyncableBlockEntity implements BloodAcceptor
{
    private float influx;

    private Heatable heatable;
    private int copperTime;

    protected HeaterBlockEntity(BlockEntityType<HeaterBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public HeaterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.HEATER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, HeaterBlockEntity blockEntity)
    {
        blockEntity.tick();
    }

    public void refreshCache(World world, BlockPos pos, BlockState state)
    {
//        if (world.getBlockEntity(pos.offset(state.get(HeaterBlock.FACING))) instanceof Heatable furnace)
        BlockPos offsetPos = pos.offset(state.get(HeaterBlock.FACING));
        heatable = Heatable.LOOKUP.find(world, offsetPos, null);
    }

    public void tick()
    {
        if (heatable == null)
        {
            refreshCache(getWorld(), getPos(), getCachedState());
        }

        if (heatable != null)
        {
            double runningRate = getPUPower();
            if (this.getPUPower() > 0.039)
            {
                heatable.setBurning();
                heatable.setHeat((float) runningRate);
                heatBlock();
            }

            Direction facing = getCachedState().get(HeaterBlock.FACING);
            BlockPos furnacePos = pos.offset(facing);
            BlockState state = getWorld().getBlockState(furnacePos);
            heatable.updateState(getWorld(), furnacePos, state);
        }
        else if (this.getPUPower() > 0.05)
        {
            heatBlock();
        }
    }

    public double getPUPower()
    {
        return influx;
    }

    @Override
    public void markRemoved()
    {
        if (this.heatable != null)
            heatable.setHeat(0);

        super.markRemoved();
    }

    public void heatBlock()
    {
        BlockPos facingPos = getPos().offset(getCachedState().get(BaseFacingBlock.FACING));
        BlockState facingState = world.getBlockState(facingPos);
        if (facingState.getBlock() instanceof Oxidizable oxidizable)
        {
            ++copperTime;
//            getWorld().addParticle(ParticleTypes.LAVA, facingPos.getX() + 0.5, facingPos.getY() + 1.5, facingPos.getZ() + 0.5, 0, 0, 0);
//            if (copperTime % 5 == 0)
//                spawnOxidiseParticles((ServerWorld) getWorld(), ParticleTypes.DRIPPING_LAVA, facingPos, new Random(world.getTime()), copperTime / 2, 16);

            if (copperTime == 100)
            {
                copperTime = 0;
                Optional<BlockState> nextBlock = oxidizable.getDegradationResult(facingState);
                if (canOxidise(world, facingPos) && nextBlock.isPresent())
                {
                    world.setBlockState(facingPos, nextBlock.get());
                }
            }
        }
        else
        {
            copperTime = 0;
        }
    }

    public static boolean canOxidise(World world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            BlockPos offset = pos.offset(direction);
            if (world.getBlockState(offset).isOf(NMFluids.PATINA_TREATMENT))
            {
                return true;
            }
        }
        return false;
    }

    protected static void spawnOxidationParticles(ServerWorld world, DefaultParticleType particle, BlockPos pos, Random random, int amount, int radius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, radius))
        {
            double dx = random.nextDouble() * 1.1;
            double dy = random.nextDouble() * 1.1;
            double dz = random.nextDouble() * 1.1;
//            ParticleSpawnPacket.send(player, particle, new Vec3d(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz), new Vec3d(0, 0, 0), amount);
//            ParticleSpawnPacket.send(player, particle, pos, amount);
        }
    }

    @Override
    public float updateInflux(float influx)
    {
        this.influx = influx;
        return influx;
    }

    @Override
    public Mode getMode()
    {
        return Mode.SINK;
    }
}
