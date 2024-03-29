package com.neep.neepmeat.mixin;

import com.neep.neepmeat.transport.event.WorldChunkEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldChunk.class)
public class WorldChunkMixin
{
    @Inject(method = "loadEntities", at = @At(value = "TAIL"))
    void onLoadEntities(CallbackInfo ci)
    {
        WorldChunkEvents.LOAD_ENTITIES.invoker().load((WorldChunk) (Object) this);
    }

    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;setWorld(Lnet/minecraft/world/World;)V"))
    void onSetBlockEntity(BlockEntity blockEntity, CallbackInfo ci)
    {
        WorldChunkEvents.BE_SET_WORLD.invoker().apply((WorldChunk) (Object) this, blockEntity);
    }

    @Inject(method = "setBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;markRemoved()V"))
    void onRemoveOldBlockEntity(BlockEntity blockEntity, CallbackInfo ci)
    {
        WorldChunkEvents.BE_MANUAL_REMOVE.invoker().apply((WorldChunk) (Object) this, blockEntity);
    }

    @Inject(method = "removeBlockEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntity;markRemoved()V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    void onRemoveBlockEntity(BlockPos pos, CallbackInfo ci, BlockEntity blockEntity)
    {
        WorldChunkEvents.BE_MANUAL_REMOVE.invoker().apply((WorldChunk) (Object) this, blockEntity);
    }
}
