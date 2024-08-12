package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.neepbus.NeepBusConfig;
import com.neep.neepmeat.neepbus.NeepBusConfigImpl;
import com.neep.neepmeat.neepbus.NeepBusPort;
import com.neep.neepmeat.neepbus.SimpleInputPort;
import com.neep.neepmeat.transport.api.pipe.AbstractBloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.block.energy_transport.VSCBlock;
import com.neep.neepmeat.transport.blood_network.BloodTransferChangeListener;
import com.neep.neepmeat.transport.screen_handler.VSCScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class VSCBlockEntity extends SyncableBlockEntity implements ExtendedScreenHandlerFactory
{
    private final SimpleInputPort inputPort = new SimpleInputPort()
    {
        @Override
        public void receive(int data)
        {
//            System.out.println(data);
            setDesiredPower(data);
        }
    };

    private final NeepBusConfig config = NeepBusConfigImpl.ofInputs(
            List.of(new NeepBusConfig.SimpleEntry("Power")),
            List.of(inputPort),
            inputPort::invalidateAddress,
            this::markDirty
    );

    protected long influx;
    private final LazyBlockApiCache<BloodAcceptor, Direction> cache;

    protected final AbstractBloodAcceptor sinkAcceptor = new AbstractBloodAcceptor()
    {
        @Override
        public Mode getMode()
        {
            return Mode.ACTIVE_SINK;
        }

        @Override
        public float updateInflux(float influx)
        {
            if (isActive() && influx * PowerUtils.referencePower() >= desiredPower)
            {
                VSCBlockEntity.this.influx = desiredPower;
                updateFrontAcceptor();
                return (float) desiredPower / PowerUtils.referencePower();
            }
            else
            {
                VSCBlockEntity.this.influx = 0;
                updateFrontAcceptor();
                return 0;
            }
        }
    };

    protected final BloodAcceptor sourceAcceptor = new AbstractBloodAcceptor()
    {
        @Override
        public Mode getMode()
        {
            return Mode.SOURCE;
        }

        @Override
        public long getOutput()
        {
            return influx;
        }
    };

    private final PropertyDelegate propertyDelegate = new VSCDelegate();

    protected int desiredPower = 0;
    protected boolean activeWithRedstone = false;

    public VSCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        cache = LazyBlockApiCache.of(BloodAcceptor.SIDED, this.pos.offset(getCachedState().get(VSCBlock.FACING)), this::getWorld, () -> getCachedState().get(VSCBlock.FACING).getOpposite());
    }

    public BloodAcceptor getBloodAcceptor(Direction face)
    {
        if (getCachedState().get(VSCBlock.FACING) == face)
        {
            return sourceAcceptor;
        }
        return sinkAcceptor;
    }

    public boolean isActive()
    {
        return getCachedState().get(VSCBlock.ACTIVE);
    }

    public void setDesiredPower(int power)
    {
        if (desiredPower != power)
        {
            desiredPower = power;
            BloodTransferChangeListener network = sinkAcceptor.getNetwork();
            if (network != null)
            {
                network.updateTransfer(sinkAcceptor);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("desired_power", desiredPower);
        nbt.putBoolean("active_with_redstone", activeWithRedstone);

        nbt.put("config", config.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.desiredPower = nbt.getInt("desired_power");
        this.activeWithRedstone = nbt.getBoolean("active_with_redstone");

        this.config.readNbt(nbt.getCompound("config"));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        buf.writeInt(desiredPower);
    }

    @Override
    public Text getDisplayName()
    {
        return Text.of("Vascular Source Converter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new VSCScreenHandler(inv, null, syncId, propertyDelegate);
    }

    @Override
    public void markRemoved()
    {
        super.markRemoved();
    }

    public void updateState(int power)
    {
        boolean prevActive = getCachedState().get(VSCBlock.ACTIVE);
        boolean nextActive = power > 0 == activeWithRedstone;
        if (nextActive != prevActive)
            updateActive(nextActive);

        updateFrontAcceptor();
    }

    private void updateActive(boolean nextActive)
    {
        getWorld().setBlockState(pos, getCachedState().with(VSCBlock.ACTIVE, nextActive));

        BloodTransferChangeListener network = sinkAcceptor.getNetwork();
        if (network != null)
        {
            network.updateTransfer(sinkAcceptor);
        }
    }

    public void updateFrontAcceptor()
    {
        BloodAcceptor found = cache.find();
        if (found != null)
        {
            found.updateInflux((float) influx / PowerUtils.referencePower());

            // Not sure how this will behave across unloads, but here it is.
            // I've also forgotten what this is supposed to do.
            found.setChangeListener(this::changed);
        }
    }

    public void changed(@Nullable BloodAcceptor changed)
    {
        BloodTransferChangeListener sinkListener = sinkAcceptor.getNetwork();
        if (sinkListener != null)
        {
            sinkListener.updateTransfer(sinkAcceptor);
        }
    }

    public void changeMode()
    {
        activeWithRedstone = !activeWithRedstone;
        updateState(getWorld().getReceivedRedstonePower(pos));
        markDirty();
    }

    public NeepBusConfig getConfig()
    {
        return config;
    }

    public void invalidatePortCache()
    {
        // Nothing to do here as there is no sender
    }

    public class VSCDelegate implements PropertyDelegate
    {
        @Override
        public int get(int index)
        {
            return switch (Names.values()[index])
            {
                case POWER_FLOW_EJ -> desiredPower;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (Names.values()[index])
            {
                case POWER_FLOW_EJ -> setDesiredPower(value);
            }
            markDirty();
        }

        @Override
        public int size()
        {
            return Names.values().length;
        }

        public enum Names
        {
            POWER_FLOW_EJ;
        }
    }
}