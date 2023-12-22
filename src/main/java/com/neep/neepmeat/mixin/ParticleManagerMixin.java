package com.neep.neepmeat.mixin;

import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ParticleManager.SimpleSpriteProvider.class)
public interface ParticleManagerMixin
{
    @Invoker("<init>")
    static ParticleManager.SimpleSpriteProvider invokeConstructor()
    {
        throw new AssertionError();
    }
}
