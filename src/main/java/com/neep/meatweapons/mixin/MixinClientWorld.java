package com.neep.meatweapons.mixin;

import com.neep.meatweapons.client.IEffectProvider;
import com.neep.meatweapons.particle.GraphicsEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Environment(value= EnvType.CLIENT)
@Mixin(ClientWorld.class)
public class MixinClientWorld implements IEffectProvider
{
    @Override
    public void addEffect(GraphicsEffect effect)
    {
    }
}
