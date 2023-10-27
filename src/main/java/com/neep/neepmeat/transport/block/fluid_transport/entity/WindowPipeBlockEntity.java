package com.neep.neepmeat.transport.block.fluid_transport.entity;

import com.neep.meatlib.blockentity.BlockEntityClientSerializable;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.fluid_network.node.BlockPipeVertex;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("UnstableApiUsage")
public class WindowPipeBlockEntity extends FluidPipeBlockEntity<WindowPipeBlockEntity.WindowPipeVertex> implements BlockEntityClientSerializable
{
    public float clientFraction;
    public long clientAmount;
    public FluidVariant clientVariant;

    public WindowPipeBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.WINDOW_PIPE, pos, state, WindowPipeVertex::new);
    }

    public WindowPipeBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, PipeConstructor<WindowPipeVertex> constructor)
    {
        super(type, pos, state, constructor);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        fromClientTag(nbt);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        toClientTag(nbt);
    }

    @Override
    public void sync()
    {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
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
    public void fromClientTag(NbtCompound nbt)
    {
        this.clientAmount = nbt.getLong("amount");
        this.clientVariant = FluidVariant.fromNbt(nbt.getCompound("variant"));
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        nbt.putLong("amount", vertex.getAmount());
        nbt.put("variant", vertex.getVariant().toNbt());
        return nbt;
    }

    public static class WindowPipeVertex extends BlockPipeVertex
    {
        public WindowPipeVertex(FluidPipeBlockEntity<WindowPipeVertex> fluidPipeBlockEntity)
        {
            super(fluidPipeBlockEntity);
        }

        @Override
        protected void onFinalCommit()
        {
            super.onFinalCommit();
            ((BlockEntityClientSerializable) parent).sync();
        }

        @Override
        public boolean canSimplify()
        {
            return false;
        }
    }
}
