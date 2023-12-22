package com.neep.meatlib.mixin;

import com.neep.meatlib.attachment.player.MeatPlayerEntity;
import com.neep.meatlib.attachment.player.PlayerAttachmentManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements MeatPlayerEntity
{
    private final PlayerAttachmentManager attachmentManager = new PlayerAttachmentManager((PlayerEntity) (Object) this);

    @Override
    public PlayerAttachmentManager neepmeat$getAttachmentManager()
    {
        return attachmentManager;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void onTick(CallbackInfo ci)
    {
        attachmentManager.tick();
    }
}
