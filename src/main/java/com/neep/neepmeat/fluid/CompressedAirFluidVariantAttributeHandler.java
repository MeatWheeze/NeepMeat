package com.neep.neepmeat.fluid;

import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.sound.SoundEvent;

import java.util.Optional;

public class CompressedAirFluidVariantAttributeHandler implements FluidVariantAttributeHandler
{
    @Override
    public Optional<SoundEvent> getFillSound(FluidVariant variant)
    {
        return Optional.of(NMSounds.COMPRESSED_AIR_FILL);
    }

    @Override
    public Optional<SoundEvent> getEmptySound(FluidVariant variant)
    {
        return Optional.of(NMSounds.COMPRESSED_AIR_FILL);
    }
}
