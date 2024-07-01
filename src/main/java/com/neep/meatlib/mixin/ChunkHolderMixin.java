package com.neep.meatlib.mixin;

import com.neep.meatlib.blockentity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ChunkHolder.class)
public abstract class ChunkHolderMixin
{
    @Shadow @Final private ChunkHolder.PlayersWatchingChunkProvider playersWatchingChunkProvider;

    @Shadow @Final ChunkPos pos;

    @Inject(method = "sendBlockEntityUpdatePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;toUpdatePacket()Lnet/minecraft/network/Packet;"), cancellable = true,
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void sendBECSUpdatePacket(World world, BlockPos pos, CallbackInfo ci, BlockEntity blockEntity)
    {
        if (blockEntity instanceof BlockEntityClientSerializable becs)
        {
            List<ServerPlayerEntity> players = playersWatchingChunkProvider.getPlayersWatchingChunk(this.pos, false);

            Packet<?> packet = blockEntity.toUpdatePacket();
            if (packet != null)
            {
                players.forEach(p -> p.networkHandler.sendPacket(packet));
            }
            else
            {
                becs.sendUpdatePacket(players);
            }

            ci.cancel();
        }
    }
}
