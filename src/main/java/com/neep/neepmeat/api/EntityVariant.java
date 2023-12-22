package com.neep.neepmeat.api;

import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface EntityVariant extends TransferVariant<EntityType<?>>
{

    class EntityVariantImpl implements EntityVariant
    {
        private final EntityType<?> type;
        private final NbtCompound nbt;
        private EntityVariantImpl(EntityType<?> entityType, NbtCompound nbt)
        {
            this.type = entityType;
            this.nbt = nbt;
        }

        public static EntityVariant of(EntityType<?> entityType, @Nullable NbtCompound tag)
        {
            Objects.requireNonNull(entityType, "EntityType may not be null.");
            return new EntityVariantImpl(entityType, tag);
        }

        @Override
        public boolean isBlank()
        {
            return false;
        }

        @Override
        public EntityType getObject()
        {
            return type;
        }

        @Override
        public @Nullable NbtCompound getNbt()
        {
            return nbt;
        }

        @Override
        public NbtCompound toNbt()
        {
            return null;
        }

        @Override
        public void toPacket(PacketByteBuf buf)
        {

        }
    }
}