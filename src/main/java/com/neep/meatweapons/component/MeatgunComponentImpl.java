package com.neep.meatweapons.component;

import com.neep.meatlib.MeatLib;
import com.neep.meatweapons.client.meatgun.RecoilManager;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.item.meatgun.MeatgunAnimationManager;
import com.neep.meatweapons.meatgun.RootModuleCache;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MeatgunComponentImpl extends ItemComponent implements MeatgunComponent
{
    @Nullable private RecoilManager recoil;
    private final RootModuleHolder holder;

    private boolean dirty = true;
    private boolean invalidated = false;
//    private final Listener listener = new Listener();

    // Eeek! A bit unsafe, but normally fine.
    @Environment(EnvType.CLIENT)
    private MeatgunAnimationManager animationManager;

    public MeatgunComponentImpl(ItemStack stack, ComponentKey<MeatgunComponent> key)
    {
        super(stack, key);
        getUuid();

        holder = RootModuleCache.getOrCreate(getUuid(), (Meatgun) stack.getItem(), !MeatLib.isClient());
        holder.setComponent(this);

        holder.readNbt(getOrCreateRootTag());
    }

    @Override
    public RootModuleHolder getRootHolder()
    {
        return holder;
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
        holder.root.trigger(world, player, stack, id, pitch, yaw, handType);
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
        holder.root.release(world, player, stack, id, pitch, yaw, handType);
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        holder.root.tickTrigger(world, player, stack, id, pitch, yaw, handType);
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
        holder.root.tick(player);

        if (!player.getWorld().isClient())
        {
            if (dirty)
            {
                holder.root.writeNbt(getOrCreateRootTag());
                dirty = false;
            }

            if (invalidated)
            {
                holder.root.readNbt(getOrCreateRootTag());
                invalidated = false;
            }
        }
    }

    @Override
    public void clientTick(PlayerEntity player)
    {
        if (animationManager == null)
            this.animationManager = MeatgunAnimationManager.getOrCreate(getUuid(), (Meatgun) stack.getItem());

        animationManager.tick(this);
    }

    @Override
    public void markDirty()
    {
        holder.root.writeNbt(getOrCreateRootTag());
    }

    @Override
    @Nullable
    public MeatgunModule find(UUID uuid)
    {
        return findRecursive(holder.root, uuid);
    }

//    @Override
//    public Listener getListener()
//    {
//        return listener;
//    }

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

//    private class Listener implements RootModuleHolder.Listener
//    {
//        @Override
//        public PacketByteBuf getBuf(MeatgunModule module)
//        {
//            PacketByteBuf buf = PacketByteBufs.create();
//            buf.writeUuid(get().getUuid());
//            buf.writeUuid(module.getUuid());
//            return buf;
//        }

//        @Override
//        public void send(PlayerEntity player, PacketByteBuf buf)
//        {
//            if (player instanceof ServerPlayerEntity serverPlayerEntity)
//                MeatgunModuleNetwork.send(serverPlayerEntity, buf);
//        }

//        @Override
//        public void markDirty(RootModuleHolder.Reason reason)
//        {
//            if (!MeatLib.isClient())
//                MeatgunComponentImpl.this.markDirty();
//        }
//    }
}
