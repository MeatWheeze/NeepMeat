package com.neep.meatweapons.meatgun;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

// Instances of this persist when the parent ItemStack is refreshed.
// Newly created MeatgunComponents take ownership of the RootModuleHolder that corresponds to the UUID stored in NBT.
public class RootModuleHolder
{
    public final MeatgunModule root;
    @Nullable private MeatgunComponent component;
    @Nullable private Set<MeatgunModule.Type<?>> moduleTypes;

    private final Listener listener = new ListenerImpl();

    // TODO: cache modules in UUID-object map

    public RootModuleHolder(UUID uuid, Meatgun meatgun)
    {
        this.root = meatgun.createBase(listener);
    }

    @Nullable
    public static MeatgunModule findRecursive(MeatgunModule module, MeatgunModule.Type<?> type)
    {
        if (module.getType() == type)
            return module;

        for (var slot : module.getChildren())
        {
            MeatgunModule child = slot.get();
            if (child == MeatgunModule.DEFAULT)
                continue;

            if (child.getType() == type)
                return child;

            MeatgunModule next = findRecursive(child, type);
            if (next != null)
                return next;
        }
        return null;
    }

    public void setComponent(@Nullable MeatgunComponent component)
    {
        this.component = component;
    }

    public void markDirty(Reason reason)
    {
        if (component != null)
            component.markDirty();

        if (reason == Reason.MODULE_SWAPPED)
        {
            moduleTypes = null;
        }
    }

    public boolean containsType(MeatgunModule.Type<?> type)
    {
        if (moduleTypes == null)
        {
            moduleTypes = new HashSet<>();
            collectTypes(root, moduleTypes);
        }

        return moduleTypes.contains(type);
    }

    private static void collectTypes(MeatgunModule root, Set<MeatgunModule.Type<?>> set)
    {
        set.add(root.getType());

        for (var slot : root.getChildren())
        {
            collectTypes(slot.get(), set);
        }
    }

    public void readNbt(NbtCompound rootTag)
    {
        root.readNbt(rootTag);
    }

    public Listener getListener()
    {
        return listener;
    }

    private class ListenerImpl implements Listener
    {
        @Override
        public RootModuleHolder getHolder()
        {
            return RootModuleHolder.this;
        }

        @Override
        public void markDirty(Reason reason)
        {
            RootModuleHolder.this.markDirty(reason);
        }
    }

    public interface Listener
    {
        RootModuleHolder getHolder();

        void markDirty(Reason reason);
    }

    public enum Reason
    {
        MODULE_SWAPPED,
        SAVE_DATA
    }
}
