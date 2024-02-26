package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
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

public class VSCBlockEntity extends SyncableBlockEntity implements ExtendedScreenHandlerFactory
{
    protected long influx;
//    private final AbstractVascularConduitEntity conduitEntity;
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
            if (influx * PowerUtils.referencePower() >= desiredPower)
            {
                VSCBlockEntity.this.influx = desiredPower;
                updateThing();
                return (float) desiredPower / PowerUtils.referencePower();
            }
            else
            {
                VSCBlockEntity.this.influx = 0;
                updateThing();
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

    int desiredPower = 0;

    public VSCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
//        conduitEntity = new AbstractVascularConduitEntity(this.pos);
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
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.desiredPower = nbt.getInt("desired_power");
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("desired_power", desiredPower);
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
//        conduitEntity.onRemove();
        super.markRemoved();
    }

    public void updateThing()
    {
        BloodAcceptor found = cache.find();
        if (found != null)
        {
            found.updateInflux((float) influx / PowerUtils.referencePower());
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