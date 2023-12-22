package com.neep.neepmeat.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.block.FlameJetBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.machine.fluid.TankBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class FlameJetBlockEntity extends SyncableBlockEntity
{
    public static final long BURN_AMOUNT = 81;

    WritableSingleFluidStorage storage = new WritableSingleFluidStorage(4 * FluidConstants.BUCKET, this::sync)
    {
        @Override
        protected boolean canInsert(FluidVariant variant)
        {
            return variant.isOf(Fluids.LAVA);
        }
    };

    protected boolean powered;

    public FlameJetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public FlameJetBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FLAME_JET, pos, state);
    }

    public Storage<FluidVariant> getFluidStorage(Direction direction)
    {
        Direction facing = getCachedState().get(FlameJetBlock.FACING);
        return direction != facing ? storage : null;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("powered", powered);
        storage.writeNbt1(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.powered = nbt.getBoolean("powered");
        storage.readNbt(nbt);
    }

    public void setPowered(boolean powered)
    {
        this.powered = powered;
        sync();
    }

    public boolean isPowered()
    {
        return powered;
    }

    public boolean canBurn()
    {
        return powered && storage.getResource().isOf(Fluids.LAVA) && storage.getAmount() >= BURN_AMOUNT;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, FlameJetBlockEntity be)
    {
        if (state.get(FlameJetBlock.RUNNING) != be.canBurn())
        {
            world.setBlockState(pos, state.cycle(FlameJetBlock.RUNNING), Block.NOTIFY_LISTENERS);
        }
        if (be.canBurn())
        {
            Direction facing = be.getCachedState().get(FlameJetBlock.FACING);
            try (Transaction transaction = Transaction.openOuter())
            {
                be.storage.extract(be.storage.getResource(), BURN_AMOUNT, transaction);
                transaction.commit();
                Box box = new Box(pos).expand(facing.getOffsetX() * 6, facing.getOffsetY() * 6, facing.getOffsetZ() * 6);
                List<LivingEntity> entityList = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), box, e -> true);
                entityList.forEach(entity ->
                {
//                    if (entity.damage(world.getDamageSources().lava(), 1.5f))
                    if (entity.damage(DamageSource.LAVA, 1.5f))
                    {
                        entity.setFireTicks(10);
                    }
                });
            }
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState blockState, FlameJetBlockEntity be)
    {
        Direction facing = blockState.get(FlameJetBlock.FACING);
        var random = world.getRandom();
        if (be.canBurn())
        {
            for (int i = 0; i < 3; ++i)
            {
                world.addParticle(ParticleTypes.FLAME,
                        pos.getX() + 0.5 + (random.nextFloat() - 0.5) / 2,
                        pos.getY() + 0.5 + (random.nextFloat() - 0.5) / 2,
                        pos.getZ() + 0.5 + (random.nextFloat() - 0.5) / 2,
                        facing.getOffsetX() / 2f, facing.getOffsetY() / 2f, facing.getOffsetZ() / 2f);
            }
        }
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        if (WritableFluidBuffer.handleInteract(storage, world, player, hand))
        {
            return true;
        }
        else if (!world.isClient())
        {
            TankBlockEntity.showContents((ServerPlayerEntity) player, world, getPos(), storage);
            return true;
        }
        return false;
    }
}
