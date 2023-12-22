package com.neep.meatlib.mixin;

import com.neep.meatlib.api.event.EntityNbtEvents;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin
{
    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void onWriteNbt(NbtCompound nbt, CallbackInfoReturnable<Boolean> cir)
    {
        EntityNbtEvents.WRITE.invoker().writeNbt((Entity) (Object) this, nbt);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    public void onReadNbt(NbtCompound nbt, CallbackInfo ci)
    {
        EntityNbtEvents.READ.invoker().writeNbt((Entity) (Object) this, nbt);
    }
}
