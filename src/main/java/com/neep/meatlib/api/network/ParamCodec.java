package com.neep.meatlib.api.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

// Ooooh, wow. Look at all the amazing things you can do with Mojang Datafixers.
// But you can't use them to spit out stuff in the desired order.
// So here's a shitty version that uses reflection.
public interface ParamCodec<T>
{
    Class<T> clazz();

    T decode(PacketByteBuf buf);

    void encode(T t, PacketByteBuf buf);

    ParamCodec<Integer> INT = of(int.class, (o, buf) -> buf.writeInt(o), PacketByteBuf::readInt);
    ParamCodec<Boolean> BOOLEAN = of(boolean.class, (o, buf) -> buf.writeBoolean(o), PacketByteBuf::readBoolean);
    ParamCodec<String> STRING = of(String.class, (o, buf) -> buf.writeString(o), PacketByteBuf::readString);
    ParamCodec<UUID> UUID = of(UUID.class, (o, buf) -> buf.writeUuid(o), PacketByteBuf::readUuid);
    ParamCodec<NbtCompound> NBT = of(NbtCompound.class, (o, buf) -> buf.writeNbt(o), PacketByteBuf::readNbt);
    ParamCodec<Identifier> IDENTIFIER = of(Identifier.class, (o, buf) -> buf.writeIdentifier(o), PacketByteBuf::readIdentifier);

    ParamCodec<PacketByteBuf> BUF = of(PacketByteBuf.class, (o, buf) ->
    {
        if (o != null)
        {
            buf.writeBoolean(true);
            buf.writeVarInt(o.readableBytes()); // Hopefully this works.
            buf.asByteBuf().writeBytes(o.asByteBuf());
        }
        else
        {
            buf.writeBoolean(false);
        }
    }, buf ->
    {
        boolean present = buf.readBoolean();
        if (present)
        {
            int bytes = buf.readVarInt();
            return PacketByteBufs.copy(PacketByteBufs.readSlice(buf, bytes));
        }
        return null;
    });


    static <T> ParamCodec<List<T>> list(ParamCodec<T> codec)
    {
        return ParamCodec.<List<T>>of((Class<List<T>>) (Object) List.class, (o, buf) ->
        {
            buf.writeInt(o.size());
            for (T t : o)
            {
                codec.encode(t, buf);
            }
        },
        buf ->
        {
            int size = buf.readInt();
            return IntStream.range(0, size).mapToObj(i -> codec.decode(buf)).toList();
        });
    }

    static <T> ParamCodec<T> of(Class<T> clazz, Encoder<T> encoder, Decoder<T> decoder)
    {
        return new ParamCodec<T>()
        {
            @Override
            public Class<T> clazz()
            {
                return clazz;
            }

            @Override
            public T decode(PacketByteBuf buf)
            {
                return decoder.decode(buf);
            }

            @Override
            public void encode(T t, PacketByteBuf buf)
            {
                encoder.encode(t, buf);
            }
        };
    }

    interface Encoder<T>
    {
        void encode(T t, PacketByteBuf buf);
    }

    interface Decoder<T>
    {
        T decode(PacketByteBuf buf);
    }
}
