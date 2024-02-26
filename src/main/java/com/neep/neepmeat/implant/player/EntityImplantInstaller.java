package com.neep.neepmeat.implant.player;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

/**
 * An implant installer that operates on an Entity.
 */
public interface EntityImplantInstaller
{
    SimpleRegistry<EntityImplantInstaller> REGISTRY = FabricRegistryBuilder.createSimple(
            EntityImplantInstaller.class, new Identifier(NeepMeat.NAMESPACE, "implant_installer")).buildAndRegister();

    void install(Entity entity);
}
