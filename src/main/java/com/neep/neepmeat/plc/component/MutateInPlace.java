package com.neep.neepmeat.plc.component;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public interface MutateInPlace<T>
{
    BlockApiLookup<MutateInPlace<ItemStack>, Void> ITEM = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "mutate_in_place_entity"),
            asClass(), Void.class
    );

    BlockApiLookup<MutateInPlace<Entity>, Void> ENTITY = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "mutate_in_place_item"),
            asClass(), Void.class
    );

    @SuppressWarnings("unchecked")
    static <T> Class<MutateInPlace<T>> asClass()
    {
        return (Class<MutateInPlace<T>>) (Object) TableComponent.class;
    }

    T get();

    void set(T t);
}
