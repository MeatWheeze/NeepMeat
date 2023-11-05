package com.neep.neepmeat.mixin;

import com.neep.neepmeat.transport.event.WorldChunkEvents;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
