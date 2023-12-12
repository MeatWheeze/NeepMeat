package com.neep.neepmeat.api;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class DataVariantImpl implements DataVariant
{
    private final DataType data;

    private static final Logger LOGGER = LogManager.getLogger("neepmeat/data");

    private DataVariantImpl(DataType data)
    {
        this.data = data;
    }

    @Override
    public boolean isBlank()
    {
        return data == DataType.BLANK;
    }

    @Override
    public DataType getObject()
    {
        return data;
    }

    @Override
    public @Nullable NbtCompound getNbt()
    {
        return null;
    }

    @Override
    public NbtCompound toNbt()
    {
        NbtCompound result = new NbtCompound();

        result.putString("id", DataType.REGISTRY.getId(data).toString());

        return result;
    }

    public static DataVariant fromNbt(NbtCompound nbt)
    {
        try
        {
            return DataVariant.of(DataType.REGISTRY.get(Identifier.tryParse(nbt.getString("id"))));
        }
        catch (RuntimeException runtimeException)
        {
            LOGGER.debug("Tried to load an invalid DataVariant from NBT: {}", nbt, runtimeException);
            return DataVariant.BLANK;
        }
    }

    public static DataVariant of(DataType data)
    {
        Objects.requireNonNull(data, "Item may not be null.");
        return new DataVariantImpl(data);
    }

    @Override
    public void toPacket(PacketByteBuf buf)
    {
        buf.writeBoolean(!isBlank());
    }
}
