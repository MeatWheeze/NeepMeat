package com.neep.meatlib.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public abstract class SyncableBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    private boolean updateComparators;
    private boolean dirty;

    public SyncableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket()
    {
//        return new SyncPacket(this);
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt()
    {
        return toClientTag(new NbtCompound());
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
    }

    @Override
    public void sync()
    {
        this.markDirty();
        softSync();
    }

    public void softSync()
    {
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    // Currently does nothing
    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        readNbt(nbt);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        writeNbt(nbt);
        return nbt;
    }

    @Override
    public void markDirty()
    {
        if (this.world != null)
        {
            world.markDirty(pos);
            this.updateComparators = true;
            BlockEntity.markDirty(this.world, this.pos, this.getCachedState());
//            dirty = true;
        }
    }

    // This can be called once every tick
    public void tryUpdateComparators()
    {
        if (updateComparators)
        {
            world.updateComparators(pos, getCachedState().getBlock());
            updateComparators = false;
        }
    }

//    public static class SyncPacket implements Packet<Client>
//    {
//        private final BlockPos pos;
//        private final BlockEntityType<?> type;
//        private final NbtCompound nbt;
//
//        public SyncPacket(SyncableBlockEntity syncableBlockEntity)
//        {
//            this(syncableBlockEntity.getPos(), syncableBlockEntity.getType(), syncableBlockEntity.toInitialChunkDataNbt());
//        }
//
//        public SyncPacket(BlockPos pos, BlockEntityType<?> type, NbtCompound nbt)
//        {
//            this.pos = pos;
//            this.type = type;
//            this.nbt = nbt.isEmpty() ? null : nbt;
//        }
//
//        public SyncPacket(PacketByteBuf buf)
//        {
//            this.pos = buf.readBlockPos();
//            this.type = buf.readRegistryValue(Registry.BLOCK_ENTITY_TYPE);
//            this.nbt = buf.readNbt();
//        }
//
//        @Override
//        public void write(PacketByteBuf buf)
//        {
//            buf.writeBlockPos(this.pos);
//            buf.writeRegistryValue(Registry.BLOCK_ENTITY_TYPE, this.type);
//            buf.writeNbt(this.nbt);
//        }
//
//        @Override
//        public void apply(Client listener)
//        {
//            Client.getInstance().apply(this);
//        }
//
//        public BlockPos getPos()
//        {
//            return pos;
//        }
//
//        public BlockEntityType<?> getType()
//        {
//            return type;
//        }
//
//        public NbtCompound getNbt()
//        {
//            return nbt;
//        }
//    }
//
//    private interface ClientPacketListener extends PacketListener
//    {
//        void apply(SyncPacket packet);
//    }
//
//    @Environment(EnvType.CLIENT)
//    private static class Client implements ClientPacketListener
//    {
//        private static final Client INSTANCE = new Client();
//        private final MinecraftClient client;
//
//        public static Client getInstance()
//        {
//            return INSTANCE;
//        }
//
//        private Client()
//        {
//            this.client = MinecraftClient.getInstance();
//        }
//
//        @Override
//        public void apply(SyncPacket packet)
//        {
//            NetworkThreadUtils.forceMainThread(packet, this, this.client);
//            BlockPos blockPos = packet.getPos();
//            this.client.world.getBlockEntity(blockPos, packet.getType()).ifPresent((blockEntity) ->
//            {
//                NbtCompound nbt = packet.getNbt();
//                if (nbt != null)
//                {
//                    ((SyncableBlockEntity) blockEntity).fromClientTag(nbt);
//                }
//            });
//        }
//
//        @Override
//        public void onDisconnected(Text reason)
//        {
//
//        }
//
//        @Override
//        public ClientConnection getConnection()
//        {
//            throw new NotImplementedException("Sorry");
//        }
//    }
}
