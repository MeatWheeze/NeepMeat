package com.neep.meatlib.mixin;

import com.neep.meatlib.api.event.DataPackPostProcess;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PlayerManager.class)
public class PlayerMangerMixin
{
    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "net/minecraft/network/packet/s2c/play/SynchronizeRecipesS2CPacket.<init>(Ljava/util/Collection;)V"))
    private void hookOnPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci)
    {
        DataPackPostProcess.SYNC.invoker().sync(player.server, Set.of(player));
    }
}
