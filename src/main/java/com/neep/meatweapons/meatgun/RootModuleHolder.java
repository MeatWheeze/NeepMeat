package com.neep.meatweapons.meatgun;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.meatgun.module.AmmunitionRequiringModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Instances of this persist when the parent ItemStack is refreshed.
// Newly created MeatgunComponents take ownership of the RootModuleHolder that corresponds to the UUID stored in NBT.
public class RootModuleHolder
{
    public final MeatgunModule root;
    private final Meatgun meatgun;
    @Nullable private MeatgunComponent component;

    @Nullable private Set<MeatgunModule.Type<?>> moduleTypes;
    @Nullable private List<MeatgunModule> modules;

    private int remainingCapacity = -1;

    private final Listener listener = new ListenerImpl();

    // TODO: cache modules in UUID-object map

    public RootModuleHolder(UUID uuid, Meatgun meatgun)
    {
        this.root = meatgun.createBase(listener);
        this.meatgun = meatgun;
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
            modules = null;
        }
    }

    public boolean containsType(MeatgunModule.Type<?> type)
    {
        cacheModules();
        return moduleTypes.contains(type);
    }

    private void cacheModules()
    {
        if (moduleTypes == null || modules == null)
        {
            moduleTypes = new HashSet<>();
            modules = new ArrayList<>();
            collectTypes(root, moduleTypes, modules);

            if (component != null)
            {
                remainingCapacity = meatgun.getMaxComplexity(component.getStack());
                for (var module : modules)
                {
                    remainingCapacity -= module.getType().complexity();
                }
            }
        }
    }

    private static void collectTypes(MeatgunModule root, Set<MeatgunModule.Type<?>> set, List<MeatgunModule> list)
    {
        set.add(root.getType());
        list.add(root);

        for (var slot : root.getChildren())
        {
            collectTypes(slot.get(), set, list);
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

    public int getRemainingComplexity()
    {
        cacheModules();
        return remainingCapacity;
    }

    public int getMaxComplexity(ItemStack stack)
    {
        return meatgun.getMaxComplexity(stack);
    }

    public boolean canSupport(MeatgunModule.Type<?> type)
    {
        cacheModules();
        return remainingCapacity >= type.complexity();
    }

    public boolean getAmmoOrReload(AmmunitionRequiringModule module, int amount, Inventory inventory, PlayerEntity player)
    {
        // TODO: Check other buffers and return true if found

        for (int i = 0; i < inventory.size(); ++i)
        {
            ItemStack stack = inventory.getStack(i);
            @Nullable AmmunitionProvider provider = AmmunitionProvider.LOOKUP.find(stack, new AmmunitionProvider.Context(inventory, i));
            if (provider != null && module.reloadFrom(provider, player))
                return false;
        }

        return false;
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
