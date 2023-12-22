package com.neep.neepmeat.player.implant;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;

public interface ImplantInstaller
{
    SimpleRegistry<ImplantInstaller> REGISTRY = FabricRegistryBuilder.createSimple(
            ImplantInstaller.class, new Identifier(NeepMeat.NAMESPACE, "implant_installer")).buildAndRegister();

    void install(Entity entity);

}
