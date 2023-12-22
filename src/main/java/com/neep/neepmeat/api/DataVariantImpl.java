package com.neep.neepmeat.api;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class DataVariantImpl implements DataVariant
{
    private final DataType data;

    private static final Logger LOGGER = LogManager.getLogger("fabric-transfer-api-v1/item");

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
        result.putBoolean("empty", isBlank());

        return result;
    }

    public static DataVariant fromNbt(NbtCompound tag)
    {
        try
        {
            if (tag.getBoolean("blank"))
            {
                return DataVariant.NORMAL;
            }
            return DataVariant.BLANK;
        }
        catch (RuntimeException runtimeException)
        {
            LOGGER.debug("Tried to load an invalid ItemVariant from NBT: {}", tag, runtimeException);
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
