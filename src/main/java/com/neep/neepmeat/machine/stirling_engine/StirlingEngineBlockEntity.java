package com.neep.neepmeat.machine.stirling_engine;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.screen_handler.StirlingEngineScreenHandler;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class StirlingEngineBlockEntity extends SyncableBlockEntity implements NamedScreenHandlerFactory, MotorEntity
{
    protected StirlingEngineStorage storage;

    public static final int MAX_ENERGY = 100;
    public static final float MULTIPLIER = 0.08f;

    public float angle;

    protected float energyStored;
    protected float prevEnergy;

    protected int burnTime;
    protected int fuelTime;

    protected BlockApiCache<Void, Void> cache = null;

//    protected SnapshotParticipant<Integer> snapshotParticipant = new SnapshotParticipant<>()
//    {
//        @Override
//        protected Integer createSnapshot()
//        {
//            return energyStored;
//        }
//
//        @Override
//        protected void readSnapshot(Integer snapshot)
//        {
//            energyStored = snapshot;
//        }
//    };

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate()
    {
        @Override
        public int get(int index)
        {
            return switch (index)
                {
                    case 0 -> burnTime;
                    case 1 -> fuelTime;
                    case 2 -> (int) energyStored;
                    default -> 0;
                };
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0 -> burnTime = value;
                case 1 -> fuelTime = value;
                case 2 -> energyStored = value;
            }
        }

        @Override
        public int size()
        {
            return 3;
        }
    };


    public StirlingEngineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new StirlingEngineStorage(this);
    }

    public StirlingEngineBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.STIRLING_ENGINE, pos, state);
    }

    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putInt("burn_time", burnTime);
        nbt.putInt("fuel_time", fuelTime);
        nbt.putFloat("energy", energyStored);
//        nbt.putInt("fuel_time", fuelTime);
    }

    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.burnTime = nbt.getInt("burn_time");
        this.fuelTime = nbt.getInt("fuel_time");
        this.energyStored = nbt.getFloat("energy");
//        this.fuelTime = nbt.getInt("fuel_time");
    }

    public void tick()
    {
        if (cache == null)
            update((ServerWorld) world, pos, pos, getCachedState());

        this.burnTime = Math.max(0, burnTime - 1);

        if (isBurning())
        {
            this.energyStored = Math.min(MAX_ENERGY, energyStored + 1);
            sync();
        }

        if (burnTime == 0)
        {
            int time;
            if (getMechPUPower() < 0.8 && (time = storage.decrementFuel()) > 0)
            {
                this.burnTime = time;
                this.fuelTime = time;
            }
            updateBurning();
        }

        if (cache != null && cache.getBlockEntity() instanceof MotorisedBlock motorised)
        {
            motorised.setInputPower((float) getMechPUPower());
            doWork();
            motorised.tick(this);
        }
    }

    public float getRunningRate()
    {
//        return this.isBurning() ? MULTIPLIER : 0;
        return energyStored / (float) MAX_ENERGY * MULTIPLIER;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, StirlingEngineBlockEntity be)
    {
        be.tick();
    }

    public boolean isBurning()
    {
        return burnTime > 0;
    }

    protected void updateBurning()
    {
        BlockState state = getCachedState();
        state = state.with(AbstractFurnaceBlock.LIT, this.isBurning());
        getWorld().setBlockState(pos, state, Block.NOTIFY_ALL);
    }

    public StirlingEngineStorage getStorage()
    {
        return storage;
    }

    @Override
    public Text getDisplayName()
    {
        return Text.translatable("container." + NeepMeat.NAMESPACE + ".stirling_engine");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new StirlingEngineScreenHandler(syncId, inv, storage.inventory, propertyDelegate);
    }

    public static float energyToSpeed(float energy)
    {
        // E=Iw^2. Assume the flywheel has a moment of inertia of 40kgm^2, divide by 20 to get velocity in rad / tick.
        float w1 = (float) Math.sqrt(2f * energy / 40) / 20;
        return (float) (w1 * 180 / Math.PI); // Convert to degrees / tick
    }

    public float doWork()
    {
        float convertAmount = 0.5f;
        if (energyStored >= convertAmount)
        {
            this.prevEnergy = energyStored;
            energyStored = Math.max(0, energyStored - convertAmount);
            sync();
            return convertAmount;
        }
        return 0;
    }

    @Override
    public void setConnectedBlock(BlockApiCache<Void, Void> motorised)
    {
        cache = motorised;
    }

    @Override
    public float getRotorAngle()
    {
        return angle;
    }

    @Override
    public float getSpeed()
    {
        return energyToSpeed(energyStored);
    }

    @Override
    public double getMechPUPower()
    {
        return getRunningRate();
    }

    @Override
    public BlockApiCache<Void, Void> getConnectedBlock()
    {
        return cache;
    }
}