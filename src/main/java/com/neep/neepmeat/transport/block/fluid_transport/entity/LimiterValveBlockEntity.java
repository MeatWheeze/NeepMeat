package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.fluid_network.node.BlockPipeVertex;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import com.neep.neepmeat.transport.screen_handler.LimiterValveScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class LimiterValveBlockEntity extends FluidPipeBlockEntity<LimiterValveBlockEntity.LimiterValveVertex> implements ExtendedScreenHandlerFactory
{
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate()
    {
        @Override
        public int get(int index)
        {
            return switch (index)
            {
                case 0 -> maxFlowRate;
                case 1 -> mbMode;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0 -> maxFlowRate = value;
                case 1 -> mbMode = value;
            }
            markDirty();
        }

        @Override
        public int size()
        {
            return LimiterValveScreenHandler.PROPERTIES;
        }
    };

    protected int maxFlowRate = 81000;
    protected int mbMode; // 0 for droplets, 1 for mb.

    public LimiterValveBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.LIMITER_VALVE, pos, state, LimiterValveVertex::new);
    }

    public LimiterValveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, PipeConstructor<LimiterValveVertex> constructor)
    {
        super(type, pos, state, constructor);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.maxFlowRate = nbt.getInt("maxFlowRate");
        this.mbMode = nbt.getInt("mbMode");
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("maxFlowRate", maxFlowRate);
        nbt.putInt("mbMode", mbMode);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return createNbt();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        buf.writeVarInt(maxFlowRate);
        buf.writeVarInt(mbMode);
    }

    @Override
    public Text getDisplayName()
    {
        return FluidTransport.LIMITER_VALVE.getName();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new LimiterValveScreenHandler(syncId, inv, propertyDelegate);
    }

    protected int getMaxFlowRate()
    {
        return maxFlowRate * (mbMode == 0 ? 1 : 81);
    }

    public static class LimiterValveVertex extends BlockPipeVertex
    {
        public LimiterValveVertex(FluidPipeBlockEntity<LimiterValveVertex> fluidPipeBlockEntity)
        {
            super(fluidPipeBlockEntity);
        }

        @Override
        public long canInsert(ServerWorld world, int inDir, FluidVariant variant, long maxAmount)
        {
            long superAmount = super.canInsert(world, inDir, variant, maxAmount);
            return Math.min(superAmount, ((LimiterValveBlockEntity) parent).getMaxFlowRate());
        }

        @Override
        public boolean canSimplify()
        {
            return false;
        }
    }
}
