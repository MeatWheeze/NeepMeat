package com.neep.meatlib.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public abstract class SyncableBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    public SyncableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        toClientTag(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        fromClientTag(nbt);
    }

    @Override
    public void sync()
    {
        this.markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {

    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void markDirty()
    {
        if (this.world != null)
        {
            world.markDirty(pos);
//            BlockEntity.markDirty(this.world, this.pos, this.getCachedState());
        }
    }
}
