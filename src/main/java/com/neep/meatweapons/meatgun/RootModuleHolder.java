package com.neep.meatweapons.meatgun;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RootModuleHolder
{
    public final MeatgunModule root;

    @Nullable private MeatgunComponent component;

    public RootModuleHolder(UUID uuid, Meatgun meatgun)
    {
        this.root = meatgun.createBase(this::markDirty);
    }

    public void setComponent(@Nullable MeatgunComponent component)
    {
        this.component = component;
    }

    public void markDirty()
    {
        if (component != null)
            component.markDirty();
    }

    public void readNbt(NbtCompound rootTag)
    {
        root.readNbt(rootTag);
    }
}
