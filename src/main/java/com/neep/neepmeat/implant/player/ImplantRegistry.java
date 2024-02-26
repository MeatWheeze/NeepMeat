package com.neep.neepmeat.implant.player;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class ImplantRegistry
{
    public static final SimpleRegistry<Constructor> REGISTRY = FabricRegistryBuilder.createSimple(
            Constructor.class, new Identifier(NeepMeat.NAMESPACE, "implant")).buildAndRegister();


    @FunctionalInterface
    public interface Constructor
    {
        EntityImplant create(Entity player);
    }
}
