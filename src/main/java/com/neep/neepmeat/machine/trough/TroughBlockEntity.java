package com.neep.neepmeat.machine.trough;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class TroughBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
    public static final FluidVariant RESOURCE = FluidVariant.of(NMFluids.STILL_FEED);
    public static final long USE_AMOUNT = FluidConstants.BUCKET / 4;

    private final WritableSingleFluidStorage storage = new WritableSingleFluidStorage(2 * FluidConstants.BUCKET, this::sync);

    public TroughBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TroughBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FEEDING_TROUGH, pos, state);
    }

    public WritableSingleFluidStorage getStorage(Direction direction)
    {
        return storage;
    }

    int age = 0;
    int waitTicks = 100;

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.toNbt(nbt);
        nbt.putInt("wait_ticks", waitTicks);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.waitTicks = nbt.getInt("wait_ticks");
    }

    @Override
    public boolean tick(MotorEntity motor)
    {
        ++age;

        if (age % waitTicks == 0)
        {
            feedAnimals();
        }

        if (world.getTime() % 10 == 0 && world instanceof ServerWorld serverWorld)
        {
//            serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, NMFluids.STILL_FEED.getDefaultState().getBlockState()),
                serverWorld.spawnParticles( NMParticles.MEAT_BIT,
                    getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5,
                    1,
                    0.5, 0.5, 0.5, 0.01);
        }

        return true;
    }

    @Override
    public void setInputPower(float power)
    {
        waitTicks = (int) MathHelper.clamp((1 - power) * 60 * 20, 100, 60 * 20);
    }

    public void feedAnimals()
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            Box box = Box.from(new BlockBox(pos).expand(5));
            List<AnimalEntity> entities = world.getEntitiesByType(
                    TypeFilter.instanceOf(AnimalEntity.class), box, e -> e.getLoveTicks() == 0 && !e.isBaby());

            Collections.shuffle(entities);

            if (entities.size() > 1 && extractFeed(TroughBlockEntity.USE_AMOUNT, transaction))
            {
                for (int i = 0; i < Math.min(2, entities.size()); ++i)
                {
                    AnimalEntity mob = entities.get(i);
                    mob.setBreedingAge(0);
                    mob.lovePlayer(null);
                }
                transaction.commit();
            }
            else transaction.abort();
        }
    }

    private boolean extractFeed(long amount, TransactionContext transaction)
    {
        if (world.getBlockEntity(pos) instanceof TroughBlockEntity be)
        {
            SingleVariantStorage<FluidVariant> storage = be.getStorage(null);
            return storage.extract(TroughBlockEntity.RESOURCE, amount, transaction) == amount;
        }
        return false;
    }

//    public void clientTick()
//    {
//        if (world.getTime() % 10 == 0)
//        {
//
//            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, NMFluids.STILL_FEED.getDefaultState().getBlockState()),
//                    );
//        }
//    }
}
