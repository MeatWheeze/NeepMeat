package com.neep.neepmeat.blockentity.machine;

import com.ibm.icu.text.MessagePattern;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatweapons.init.GraphicsEffects;
import com.neep.meatweapons.network.BeamPacket;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.neepmeat.block.machine.HeaterBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.mixin.FurnaceAccessor;
import com.neep.neepmeat.network.ParticleSpawnPacket;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.Random;

public class HeaterBlockEntity extends BloodMachineBlockEntity
{
    public static long USE_AMOUNT = FluidConstants.BUCKET / 300;
    public static long CAPACITY = 4 * USE_AMOUNT;

    protected FurnaceAccessor accessor;
    protected int copperTime;

    protected HeaterBlockEntity(BlockEntityType<HeaterBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, CAPACITY, CAPACITY);
    }

    public HeaterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.HEATER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, HeaterBlockEntity blockEntity)
    {
        blockEntity.tick();
    }

    public boolean refreshCache(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos.offset(state.get(HeaterBlock.FACING))) instanceof FurnaceAccessor furnace)
        {
            accessor = furnace;
            return true;
        }
        else
        {
            accessor = null;
            return false;
        }
    }

    public void tick()
    {
        if (accessor == null)
        {
            refreshCache(getWorld(), getPos(), getCachedState());
        }

        Transaction transaction = Transaction.openOuter();
        long work = doWork(USE_AMOUNT, transaction);
        if (work == USE_AMOUNT)
        {
            if (accessor != null)
                accessor.setBurnTime(2);

            heatBlock();
        }
        transaction.commit();
    }

    @Override
    public void onUse(PlayerEntity player, Hand hand)
    {
        if (player.isSneaking())
        {
            clearBuffers();
            getWorld().playSound(null, getPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
        }
        player.sendMessage(Text.of((inputBuffer.getAmount())
                + ", "
                + (outputBuffer.getAmount())), true);
        getWorld().playSound(null, getPos(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1.5f);
    }

    public static void updateBlockState(FurnaceAccessor accessor, World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        state = state.with(AbstractFurnaceBlock.LIT, accessor.getBurnTime() > 0);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
    }

    public void heatBlock()
    {
        BlockPos facingPos = getPos().offset(getCachedState().get(BaseFacingBlock.FACING));
        BlockState facingState = world.getBlockState(facingPos);
        if (facingState.getBlock() instanceof OxidizableBlock)
        {
            ++copperTime;
//            getWorld().addParticle(ParticleTypes.LAVA, facingPos.getX() + 0.5, facingPos.getY() + 1.5, facingPos.getZ() + 0.5, 0, 0, 0);
            if (copperTime % 5 == 0)
//                spawnOxidiseParticles((ServerWorld) getWorld(), ParticleTypes.DRIPPING_LAVA, facingPos, new Random(world.getTime()), copperTime / 2, 16);

            if (copperTime == 100)
            {
                copperTime = 0;
                Optional<Block> nextBlock = Oxidizable.getIncreasedOxidationBlock(facingState.getBlock());
                if (canOxidise(world, facingPos) && nextBlock.isPresent())
                {
                    world.setBlockState(facingPos, nextBlock.get().getDefaultState());
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

    protected static void spawnOxidiseParticles(ServerWorld world, DefaultParticleType particle, BlockPos pos, Random random, int amount, int radius)
    {
        for (ServerPlayerEntity player : PlayerLookup.around(world, pos, radius))
        {
            double dx = random.nextDouble() * 1.1;
            double dy = random.nextDouble() * 1.1;
            double dz = random.nextDouble() * 1.1;
//            ParticleSpawnPacket.send(player, particle, new Vec3d(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz), new Vec3d(0, 0, 0), amount);
            ParticleSpawnPacket.send(player, particle, pos, amount);
        }
    }
}
