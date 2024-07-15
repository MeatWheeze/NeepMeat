package com.neep.meatweapons.component;

import com.neep.meatlib.MeatLib;
import com.neep.meatweapons.client.meatgun.RecoilManager;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.item.meatgun.MeatgunAnimationManager;
import com.neep.meatweapons.meatgun.module.BasePistolModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunModuleNetwork;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MeatgunComponentImpl extends ItemComponent implements MeatgunComponent
{
    @Nullable private RecoilManager recoil;
    private final MeatgunModule root;

    private boolean dirty = true;
    private boolean invalidated = false;
    private final Listener listener = new Listener();

    // Eeek! A bit unsafe, but normally fine.
    @Environment(EnvType.CLIENT)
    private MeatgunAnimationManager animationManager;

    // TODO: cache modules in UUID-object map

    public MeatgunComponentImpl(ItemStack stack, ComponentKey<MeatgunComponent> key)
    {
        super(stack, key);

        if (stack.getItem() instanceof Meatgun meatgun)
            root = meatgun.createBase(listener);
        else
            // Fail silently in case someone is doing something weird that I can't control.
            // I have no examples.
            root = new BasePistolModule(listener);

        root.readNbt(getOrCreateRootTag());
        getUuid();
    }

    @Override
    public MeatgunModule getRoot()
    {
        return root;
    }

    @Override
    public UUID getUuid()
    {
        UUID foundUUID = getUuid("meatgun_uuid");
        if (foundUUID == null)
        {
            putUuid("meatgun_uuid", UUID.randomUUID());
            return getUuid("meatgun_uuid");
        }
        return foundUUID;
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        root.trigger(world, player, stack, id, pitch, yaw, handType);
//        var module = root.getChildren().get(0).get();
//        if (module instanceof BosherModule)
//        {
//            root.getChildren().get(0).set(new UnderbarrelModule());
//            markDirty();
//        }
//        else
//        {
//            root.getChildren().get(0).set(new BosherModule());
//            markDirty();
//        }
    }

    @Override
    public void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        root.release(world, player, stack, id, pitch, yaw, handType);
//        var module = root.getChildren().get(0).get();
//        if (module instanceof BosherModule)
//        {
//            root.getChildren().get(0).set(new UnderbarrelModule());
//            markDirty();
//        }
//        else
//        {
//            root.getChildren().get(0).set(new BosherModule());
//            markDirty();
//        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        root.tickTrigger(world, player, stack, id, pitch, yaw, handType);
    }

    @Override
    public RecoilManager getRecoil()
    {
        if (recoil == null)
            recoil = RecoilManager.getOrCreate(getUuid());

        return recoil;
    }

    @Override
    public @Nullable MeatgunAnimationManager getAnimationManager()
    {
        return animationManager;
    }

    @Override
    public void commonTick(PlayerEntity player)
    {
        root.tick(player);

        if (!player.getWorld().isClient())
        {
            if (dirty)
            {
                root.writeNbt(getOrCreateRootTag());
                dirty = false;
            }

            if (invalidated)
            {
                root.readNbt(getOrCreateRootTag());
                invalidated = false;
            }
        }
    }

    @Override
    public void clientTick(PlayerEntity player)
    {
        if (animationManager == null)
            this.animationManager = (MeatgunAnimationManager) ((Meatgun) stack.getItem()).createAnimationManager(this).get();

        animationManager.tick();
    }

    @Override
    public void markDirty()
    {
//        dirty = true;
        if (root != null)
            root.writeNbt(getOrCreateRootTag());
    }

    @Override
    @Nullable
    public MeatgunModule find(UUID uuid)
    {
        return findRecursive(root, uuid);
    }

    @Override
    public Listener getListener()
    {
        return listener;
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

    @Nullable
    private MeatgunModule findRecursive(MeatgunModule module, UUID uuid)
    {
        if (module.getUuid().equals(uuid))
            return module;

        for (var slot : module.getChildren())
        {
            MeatgunModule child = slot.get();
            if (child == MeatgunModule.DEFAULT)
                continue;

            if (child.getUuid().equals(uuid))
                return child;

            MeatgunModule next = findRecursive(child, uuid);
            if (next != null)
                return next;
        }
        return null;
    }

    @Override
    public void onTagInvalidated()
    {
        super.onTagInvalidated();
        dirty = true;
        invalidated = true;
    }

    public int getInt()
    {
        return getInt("ooer");
    }

    public void writeInt(int i)
    {
        putInt("ooer", i);
    }

    private class Listener implements MeatgunComponent.Listener
    {
        @Override
        public MeatgunComponent get()
        {
            return MeatgunComponentImpl.this;
        }

        @Override
        public PacketByteBuf getBuf(MeatgunModule module)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeUuid(get().getUuid());
            buf.writeUuid(module.getUuid());
            return buf;
        }

        @Override
        public void send(PlayerEntity player, PacketByteBuf buf)
        {
            if (player instanceof ServerPlayerEntity serverPlayerEntity)
                MeatgunModuleNetwork.send(serverPlayerEntity, buf);
        }

        @Override
        public void markDirty()
        {
            if (!MeatLib.isClient())
                MeatgunComponentImpl.this.markDirty();
        }
    }
}
