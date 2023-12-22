package com.neep.meatweapons.mixin;

import com.neep.meatweapons.entity.PlayerWeaponManager;
import com.neep.meatweapons.interfaces.MWPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements MWPlayerEntity
{
    protected PlayerWeaponManager manager = new PlayerWeaponManager((PlayerEntity) (Object) this);

    @Override
    public PlayerWeaponManager meatweapons$getWeaponManager()
    {
        return manager;
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    void onTick(CallbackInfo ci)
    {
        manager.tick();
    }
}
