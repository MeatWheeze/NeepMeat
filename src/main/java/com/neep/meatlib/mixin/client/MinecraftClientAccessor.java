package com.neep.meatlib.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor
{
    @Accessor("itemUseCooldown")
    void setUseItemCooldown(int cooldown);
}
