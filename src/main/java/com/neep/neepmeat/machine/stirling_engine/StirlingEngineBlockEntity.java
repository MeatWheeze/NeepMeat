package com.neep.neepmeat.machine.stirling_engine;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.block.IntegratorBlock;
import com.neep.neepmeat.block.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import com.neep.neepmeat.screen_handler.StirlingEngineScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class StirlingEngineBlockEntity extends SyncableBlockEntity implements NamedScreenHandlerFactory, IMotorBlockEntity
{
    protected StirlingEngineStorage storage;

    public static final int MAX_ENERGY = 8192;

    public float angle;

    protected int energyStored;
    private int prevEnergy;

    protected int burnTime;
    protected int fuelTime;

    protected IMotorisedBlock cache = null;

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
                    case 2 -> energyStored;
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
        nbt.putInt("energy", energyStored);
//        nbt.putInt("fuel_time", fuelTime);
    }

    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.burnTime = nbt.getInt("burn_time");
        this.fuelTime = nbt.getInt("fuel_time");
        this.energyStored = nbt.getInt("energy");
//        this.fuelTime = nbt.getInt("fuel_time");
    }

    public void tick()
    {
        if (cache == null)
            update(world, pos, pos, getCachedState());

        this.burnTime = Math.max(0, burnTime - 1);

        if (isBurning())
        {
            this.energyStored = Math.min(MAX_ENERGY, energyStored + 2);
            sync();
        }

        if (burnTime == 0)
        {
            int time;
            if (getWorkMultiplier() < 0.8 && (time = storage.decrementFuel()) > 0)
            {
                this.burnTime = time;
                this.fuelTime = time;
            }
            updateBurning();
        }

        if (cache != null)
        {
            cache.setWorkMultiplier(getWorkMultiplier());
            doWork();

            if (!cache.tick(this))
                energyStored = prevEnergy;
        }
    }

    public float getWorkMultiplier()
    {
        return energyStored / (float) MAX_ENERGY;
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
        return new TranslatableText("container." + NeepMeat.NAMESPACE + ".stirling_engine");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new StirlingEngineScreenHandler(syncId, inv, storage.inventory, propertyDelegate);
    }

    public static float energyToSpeed(int energy)
    {
        // E=Iw^2. Assume the flywheel has a moment of inertia of 100kgm^2, divide by 20 to get velocity in rad / tick.
        float w1 = (float) Math.sqrt(2f * energy / 100) / 20;
        return (float) (w1 * 180 / Math.PI); // Convert to degrees / tick
    }

    public int doWork()
    {
        int convertAmount = 1;
        if (energyStored > convertAmount)
        {
            this.prevEnergy = energyStored;
            energyStored -= convertAmount;
            sync();
            return convertAmount;
        }
        return 0;
    }

    @Override
    public void setConnectedBlock(IMotorisedBlock motorised)
    {
        cache = motorised;
    }

    @Override
    public IMotorisedBlock getConnectedBlock()
    {
        return cache;
    }
}