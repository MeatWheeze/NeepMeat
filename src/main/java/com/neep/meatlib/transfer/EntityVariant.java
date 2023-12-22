package com.neep.meatlib.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class EntityVariant<T extends Entity> implements TransferVariant<EntityType<T>>
{
    @Nullable
    protected final EntityType<T> entityType;
    @Nullable
    protected final NbtCompound nbt;
    private final int hashCode;

    protected static final EntityVariant<?> BLANK = new EntityVariant<>(null, null);

    protected EntityVariant(@Nullable EntityType<T> entityType, @Nullable NbtCompound nbt)
    {
        this.entityType = entityType;

        if (nbt != null)
            this.nbt = nbt.copy();
        else
            this.nbt = null;

        this.hashCode = Objects.hash(entityType, nbt);
    }

    public static <T extends Entity> EntityVariant<T> of(EntityType<T> type)
    {
        Objects.requireNonNull(type, "EntityType may not be null.");

        // TODO: Store specific entity details rather than just type
        return new EntityVariant<>(type, null);
    }

    public static <T extends Entity> EntityVariant<T> of(Entity entity)
    {
        Objects.requireNonNull(entity, "Entity may not be null.");

        // TODO: Store specific entity details rather than just type
        return new EntityVariant<>((EntityType<T>) entity.getType(), null);
    }

    public static EntityVariant<?> getBlank()
    {
        return BLANK;
    }

    @Override
    public boolean isBlank()
    {
        return entityType == null;
    }

    @Override
    public EntityType<T> getObject()
    {
        return entityType;
    }

    @Override
    public @Nullable NbtCompound getNbt()
    {
        return nbt;
    }

    @Override
    public NbtCompound toNbt()
    {
        throw new NotImplementedException();
//        return ;
    }

    @Override
    public void toPacket(PacketByteBuf buf)
    {
        throw new NotImplementedException();
    }

    @Override
    public String toString()
    {
        return "FluidVariantImpl{fluid=" + entityType + ", tag=" + nbt + '}';
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }
}
