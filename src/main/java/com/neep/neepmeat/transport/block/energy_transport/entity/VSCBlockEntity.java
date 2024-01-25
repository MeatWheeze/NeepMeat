package com.neep.neepmeat.transport.block.energy_transport.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.transport.api.pipe.AbstractBloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.block.energy_transport.VSCBlock;
import com.neep.neepmeat.transport.blood_network.BloodNetwork;
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

public class VSCBlockEntity extends SyncableBlockEntity implements ExtendedScreenHandlerFactory, VascularConduitEntity
{
    protected long influx;
    @Nullable private BloodNetwork network;

    protected final AbstractBloodAcceptor backAcceptor = new AbstractBloodAcceptor()
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
                return (float) desiredPower / PowerUtils.referencePower();
            }
            else
            {
                VSCBlockEntity.this.influx = 0;
                return 0;
            }
        }
    };

    protected final BloodAcceptor frontAcceptor = new AbstractBloodAcceptor()
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
    }

    public BloodAcceptor getBloodAcceptor(Direction face)
    {
        if (getCachedState().get(VSCBlock.FACING) == face.getOpposite())
        {
            return backAcceptor;
        }
        return frontAcceptor;
    }

    public void setDesiredPower(int power)
    {
        if (desiredPower != power)
        {
            desiredPower = power;
            BloodNetwork network = backAcceptor.getNetwork();
            if (network != null)
            {
                network.updateTransfer(backAcceptor);
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
    public @Nullable BloodNetwork getNetwork()
    {
        return network;
    }

    @Override
    public void setNetwork(@Nullable BloodNetwork network)
    {
        this.network = network;
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