package com.neep.meatweapons.component;

import com.neep.meatweapons.client.meatgun.RecoilManager;
import com.neep.meatweapons.item.meatgun.MeatgunAnimationManager;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface MeatgunComponent extends Component
{
    RootModuleHolder getRootHolder();

    UUID getUuid();

    void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType);

    void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType);

    void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType);

    void commonTick(PlayerEntity player);

    void clientTick(PlayerEntity player);

    void markDirty();

    @Nullable
    MeatgunModule find(UUID uuid);

    RecoilManager getRecoil();

    @Nullable MeatgunAnimationManager getAnimationManager();
}
