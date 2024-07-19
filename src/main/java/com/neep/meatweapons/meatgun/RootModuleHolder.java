package com.neep.meatweapons.meatgun;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.meatgun.module.AmmunitionRequiringModule;
import com.neep.meatweapons.meatgun.module.AmmunitionStoringModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.network.MeatgunNetwork;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

// Instances of this persist when the parent ItemStack is refreshed.
// Newly created MeatgunComponents take ownership of the RootModuleHolder that corresponds to the UUID stored in NBT.
public class RootModuleHolder
{
    public final MeatgunModule root;
    private final Meatgun meatgun;
    @Nullable private MeatgunComponent component;

    @Nullable private Set<MeatgunModule.Type<?>> moduleTypes;
    @Nullable private List<MeatgunModule> modules;
    @Nullable private Map<AmmunitionType, List<AmmunitionStoringModule>> ammunition;

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
        if (moduleTypes == null || modules == null || ammunition == null)
        {
            moduleTypes = new HashSet<>();
            modules = new ArrayList<>();
            collectTypes(root, moduleTypes, modules);

            // Sort each module by the type it provides for quick(er than the alternative) access via an EnumMap
            ammunition = modules.stream().filter(m -> m instanceof AmmunitionStoringModule).map(m -> (AmmunitionStoringModule) m)
                    .collect(Collectors.groupingBy(AmmunitionStoringModule::ammoType, () -> new EnumMap<>(AmmunitionType.class), Collectors.toList()));

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

    public int getAmmo(AmmunitionType type)
    {
        cacheModules();
        return ammunition.getOrDefault(type, List.of()).stream().mapToInt(AmmunitionStoringModule::amount).sum();
    }

    public Map<AmmunitionType, List<AmmunitionStoringModule>> getAmmo()
    {
        cacheModules();
        return ammunition;
    }

    public boolean getAmmoOrReload(AmmunitionRequiringModule module, int amount, Inventory inventory, PlayerEntity player)
    {
//        if (player.isCreative())
//            return true;

        cacheModules();
        int available = getAmmo(module.ammoType());

        if (available >= amount)
        {
            int required = amount;
            for (var otherModule : modules)
            {
                if (otherModule instanceof AmmunitionStoringModule storage
                        && storage.ammoType() == module.ammoType()
                        && storage.amount() >= amount)
                {
                    required -= storage.extract(required);
                    if (required == 0)
                        return true;
                }
            }
        }


        boolean reloaded = false;
        for (int i = 0; i < inventory.size(); ++i)
        {
            ItemStack stack = inventory.getStack(i);
            @Nullable AmmunitionProvider provider = AmmunitionProvider.LOOKUP.find(stack, new AmmunitionProvider.ContextImpl(inventory, i));
//            if (provider != null && module.reloadFrom(provider, player))
//                return false;
            if (provider != null && provider.ammoType() == module.ammoType())
            {
                reload(provider, player);
            }
        }


        return false;
    }

    public boolean reload(AmmunitionProvider provider, PlayerEntity player)
    {
        cacheModules();
        int current = getAmmo(provider.ammoType());
        if (current >= provider.getAmount())
            return false;

        int remaining = provider.getAmount() - current;
        for (var otherModule : modules)
        {
            if (otherModule instanceof AmmunitionStoringModule storage
                    && storage.ammoType() == provider.ammoType())
            {
                remaining -= storage.insert(remaining);
                if (remaining <= 0)
                    break;
            }
        }

        if (remaining < provider.getAmount())
        {
            provider.consume();
            listener.markDirty(Reason.SAVE_DATA);
            player.playSound(NMSounds.RELOAD_MECHANICAL_HISS, SoundCategory.PLAYERS, 1, 1);
            MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("reload", PacketByteBufs.create().writeVarInt(10));
            return true;
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
