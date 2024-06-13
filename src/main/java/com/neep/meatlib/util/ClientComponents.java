package com.neep.meatlib.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ClientComponents
{
    private static final Map<BlockEntityType<?>, Constructor<?>> MAP = new HashMap<>();

    public static <T extends BlockEntity> void register(BlockEntityType<T> type, Constructor<T> constructor)
    {
        MAP.put(type, constructor);
    }

    public static <T extends BlockEntity, C extends ClientComponent> ClientComponent get(T be)
    {
        Constructor<T> constructor = (Constructor<T>) MAP.get(be.getType());
        if (constructor == null)
            throw new IllegalStateException("No client component registered for type '" + be.getType() + "'");

        return constructor.get(be);
    }

    public static class Holder<T extends BlockEntity>
    {
        private final T be;

        public Holder(T be)
        {
            this.be = be;
        }

        @Nullable
        private ClientComponent component = null;

        public ClientComponent get()
        {
            if (component == null)
            {
                component = ClientComponents.get(be);
            }
            return component;
        }
    }

    @FunctionalInterface
    public interface Constructor<T>
    {
        ClientComponent get(T t);
    }
}
