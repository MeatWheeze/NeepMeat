package com.neep.meatweapons.component;

import com.neep.meatweapons.client.meatgun.RecoilManager;
import com.neep.meatweapons.item.TriggerReceiver;
import com.neep.meatweapons.item.meatgun.MeatgunAnimationManager;
import com.neep.meatweapons.meatgun.RootModuleHolder;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface MeatgunComponent extends Component, TriggerReceiver
{
    RootModuleHolder getRootHolder();

    UUID getUuid();

    void commonTick(PlayerEntity player);

    void clientTick(PlayerEntity player);

    void markDirty();

    @Nullable
    MeatgunModule find(UUID uuid);

    RecoilManager getRecoil();

    @Nullable MeatgunAnimationManager getAnimationManager();

    ItemStack getStack();
}
