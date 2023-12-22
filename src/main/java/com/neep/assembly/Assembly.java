package com.neep.assembly;

import com.neep.assembly.client.renderer.AssemblyRenderer;
import com.neep.neepmeat.NeepMeat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Assembly implements ModInitializer, ClientModInitializer
{

    public static final String NAMESPACE = "assembly";

    public static EntityType<AssemblyEntity> ASSEMBLY_ENTITY;

    public <T extends Entity> EntityType<T> registerEntity(String id, EntityType.EntityFactory<T> factory)
    {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(NAMESPACE, id),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory).build());
    }

    @Override
    public void onInitialize()
    {
//        FabricDefaultAttributeRegistry.register(ASSEMBLY_ENTITY, AssemblyEntity.createMobAttributes());
        ASSEMBLY_ENTITY = registerEntity("assembly", AssemblyEntity::new);
    }

    @Override
    public void onInitializeClient()
    {
        EntityRendererRegistry.register(ASSEMBLY_ENTITY, AssemblyRenderer::new);
    }
}
