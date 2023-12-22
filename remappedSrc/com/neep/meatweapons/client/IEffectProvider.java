package com.neep.meatweapons.client;

import com.neep.meatweapons.particle.BeamGraphicsEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value= EnvType.CLIENT)
public interface IEffectProvider
{
    default void addEffect(BeamGraphicsEffect effect)
    {

    }
}
