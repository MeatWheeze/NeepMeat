package com.neep.meatweapons.init;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.MeatgunComponentImpl;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class MWComponents implements ItemComponentInitializer, WorldComponentInitializer
{
    public static final ComponentKey<MeatgunComponent> MEATGUN =
            ComponentRegistry.getOrCreate(
                    new Identifier(MeatWeapons.NAMESPACE, "meatgun"),
                    MeatgunComponent.class);

//    public static final ComponentKey<MeatgunParticleManager> MEATGUN_PARTICLES =
//            ComponentRegistry.getOrCreate(
//                    new Identifier(MeatWeapons.NAMESPACE, "meatgun_particle_manager"),
//                    MeatgunParticleManager.class);

    @Override
    public void registerItemComponentFactories(@NotNull ItemComponentFactoryRegistry registry)
    {
        registry.register(MWItems.MEATGUN_PISTOL, MEATGUN, stack -> new MeatgunComponentImpl(stack, MEATGUN));
        registry.register(MWItems.MEATGUN_STAFF, MEATGUN, stack -> new MeatgunComponentImpl(stack, MEATGUN));
    }

    @Override
    public void registerWorldComponentFactories(@NotNull WorldComponentFactoryRegistry registry)
    {
//        registry.register(MEATGUN_PARTICLES, MeatgunParticleManager::new);
    }
}
