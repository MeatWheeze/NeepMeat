package com.neep.neepmeat.machine.stirling_engine;

import com.neep.meatlib.block.BaseFacingBlock;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class StirlingEngineBlockEntity extends SyncableBlockEntity implements NamedScreenHandlerFactory, MotorEntity
{
    protected StirlingEngineStorage storage;

    public static final int MAX_THINGY = 100;
    public static final float MAX_PU = 0.08f;

    public float angle;

    protected float thingyStored;
    protected float prevEnergy;

    protected int burnTime;
    protected int fuelTime;

    protected BlockApiCache<MotorisedBlock, Void> cache = null;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate()
    {
        @Override
        public int get(int index)
        {
            return switch (index)
                {
                    case 0 -> burnTime;
                    case 1 -> fuelTime;
                    case 2 -> (int) thingyStored;
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
                case 2 -> thingyStored = value;
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
        nbt.putFloat("energy", thingyStored);
    }

    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.burnTime = nbt.getInt("burn_time");
        this.fuelTime = nbt.getInt("fuel_time");
        this.thingyStored = nbt.getFloat("energy");
    }

    public void tick()
    {
        if (cache == null)
        {
            Direction facing = getCachedState().get(BaseFacingBlock.FACING);
            cache = BlockApiCache.create(MotorisedBlock.LOOKUP, (ServerWorld) world, pos.offset(facing));
        }

        this.burnTime = Math.max(0, burnTime - 1);

        float newThingyStored = thingyStored;
        if (isBurning())
        {
            newThingyStored = Math.min(MAX_THINGY, thingyStored + 1);
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

        MotorisedBlock motorised = cache.find(null);
        if (motorised != null)
        {
            motorised.setInputPower((float) getMechPUPower());
            doWork();
            motorised.motorTick(this);
        }

        if (newThingyStored != thingyStored)
        {
            thingyStored = newThingyStored;
            sync();
        }
    }

    public float getRunningRate()
    {
        return thingyStored / (float) MAX_THINGY * MAX_PU;
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

    public void doWork()
    {
        float convertAmount = 0.5f;
        if (thingyStored >= convertAmount)
        {
            this.prevEnergy = thingyStored;
            thingyStored = Math.max(0, thingyStored - convertAmount);
        }
    }

    @Override
    public float getRotorAngle()
    {
        return angle;
    }

    @Override
    public float getSpeed()
    {
        return energyToSpeed(thingyStored);
    }

    @Override
    public double getMechPUPower()
    {
        return getRunningRate();
    }

    @Override
    public MotorisedBlock getConnectedBlock()
    {
        return cache != null ? cache.find(null) : null;
    }
}