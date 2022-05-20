package com.neep.assembly;

import com.neep.assembly.block.AnchorBlock;
import com.neep.assembly.client.renderer.AssemblyRenderer;
import com.neep.assembly.client.renderer.BoatTestRenderer;
import com.neep.neepmeat.NMItemGroups;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Assembly implements ModInitializer, ClientModInitializer
{

    public static final String NAMESPACE = "assembly";

    public static EntityType<AssemblyEntity> ASSEMBLY_ENTITY;
    public static EntityType<BoatTest> BOAT_TEST;

    public static Block PLATFORM = new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static Item PLATFORM_ITEM;

    public static Block ANCHOR = new AnchorBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f));
    public static Item ANCHOR_ITEM;

    public <T extends Entity> EntityType<T> registerEntity(String id, EntityType.EntityFactory<T> factory)
    {
        return Registry.register(Registry.ENTITY_TYPE, new Identifier(NAMESPACE, id),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory).build());
    }

    @Override
    public void onInitialize()
    {
//        MeatLib.setNamespace(NAMESPACE);
        ASSEMBLY_ENTITY = registerEntity("assembly", AssemblyEntity::new);
        BOAT_TEST = registerEntity("boat_test", BoatTest::new);

        PLATFORM = Registry.register(Registry.BLOCK, new Identifier(NAMESPACE, "platform"), PLATFORM);
        PLATFORM_ITEM = Registry.register(Registry.ITEM, new Identifier(NAMESPACE, "platform"), new BlockItem(PLATFORM,
                new FabricItemSettings().group(NMItemGroups.GENERAL)));

        ANCHOR = Registry.register(Registry.BLOCK, new Identifier(NAMESPACE, "anchor"), ANCHOR);
        ANCHOR_ITEM = Registry.register(Registry.ITEM, new Identifier(NAMESPACE, "anchor"), new BlockItem(ANCHOR,
                new FabricItemSettings().group(NMItemGroups.GENERAL)));

        Registry.register(Registry.ITEM, new Identifier(NAMESPACE, "assembly_debug"), new DebugItem(
                new FabricItemSettings().group(NMItemGroups.GENERAL)));
    }

    @Override
    public void onInitializeClient()
    {
        EntityRendererRegistry.register(ASSEMBLY_ENTITY, AssemblyRenderer::new);
        EntityRendererRegistry.register(BOAT_TEST, BoatTestRenderer::new);
    }

    static
    {
//        MeatLib.setNamespace(NAMESPACE);
//        PLATFORM = BlockRegistry.queueBlock(new Block(FabricBlockSettings.of(Material.METAL).strength(4.0f)), "platform");
    }
}
