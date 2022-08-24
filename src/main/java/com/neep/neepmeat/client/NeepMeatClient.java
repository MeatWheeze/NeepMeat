package com.neep.neepmeat.client;

import com.neep.meatlib.block.BasePaintedBlock;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.fluid.OreFatAttributeHandler;
import com.neep.neepmeat.client.fluid.OreFatFluidVariantRenderHandler;
import com.neep.neepmeat.client.hud.HUDOverlays;
import com.neep.neepmeat.client.model.GenericModel;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.client.renderer.*;
import com.neep.neepmeat.client.screen.*;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.machine.casting_basin.CastingBasinRenderer;
import com.neep.neepmeat.machine.crucible.AlembicRenderer;
import com.neep.neepmeat.machine.crucible.CrucibleRenderer;
import com.neep.neepmeat.machine.grinder.GrinderRenderer;
import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressRenderer;
import com.neep.neepmeat.machine.mixer.MixerRenderer;
import com.neep.neepmeat.machine.motor.MotorRenderer;
import com.neep.neepmeat.machine.multitank.MultiTankRenderer;
import com.neep.neepmeat.machine.pedestal.PedestalRenderer;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelRenderer;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineRenderer;
import com.neep.neepmeat.network.ParticleSpawnPacket;
import com.neep.neepmeat.network.TankMessagePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class NeepMeatClient implements ClientModInitializer
{
    public static final EntityModelLayer MODEL_GLASS_TANK_LAYER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "glass_tank"), "main");
    public static final EntityModelLayer TANK_MINECART = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "tank_minecart"), "main");

    public static final Identifier BLOOD_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/blood_flowing");
    public static final Identifier BLOOD = new Identifier(NeepMeat.NAMESPACE, "block/blood_still");
    public static final Identifier CHARGED_WORK_FLUID_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/charged_work_fluid_flowing");
    public static final Identifier CHARGED_WORK_FLUID= new Identifier(NeepMeat.NAMESPACE, "block/charged_work_fluid_still");
    public static final Identifier WORK_FLUID_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/work_fluid_flowing");
    public static final Identifier WORK_FLUID = new Identifier(NeepMeat.NAMESPACE, "block/work_fluid_still");
    public static final Identifier ETHEREAL_FUEL_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/ethereal_fuel_flowing");
    public static final Identifier ETHEREAL_FUEL = new Identifier(NeepMeat.NAMESPACE, "block/ethereal_fuel_still");
    public static final Identifier ELDRITCH_ENZYMES_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/eldritch_enzymes_flowing");
    public static final Identifier ELDRITCH_ENZYMES = new Identifier(NeepMeat.NAMESPACE, "block/eldritch_enzymes_still");
    public static final Identifier DIRTY_ORE_FAT_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/dirty_ore_fat_flowing");
    public static final Identifier DIRTY_ORE_FAT= new Identifier(NeepMeat.NAMESPACE, "block/dirty_ore_fat_still");
    public static final Identifier CLEAN_ORE_FAT_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/clean_ore_fat_flowing");
    public static final Identifier CLEAN_ORE_FAT= new Identifier(NeepMeat.NAMESPACE, "block/clean_ore_fat_still");
    public static final Identifier MEAT = new Identifier(NeepMeat.NAMESPACE, "block/meat_still");
    public static final Identifier MEAT_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/meat_flowing");

    @Override
    public void onInitializeClient()
    {
        registerRenderers();
        registerLayers();
        registerScreens();
        HUDOverlays.init();

        TankMessagePacket.Client.registerReciever();
        ParticleSpawnPacket.Client.registerReceiver();
    }

    public static void registerRenderers()
    {
        // Custom baked models
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new NeepMeatModelProvider());
        ModelLoadingRegistry.INSTANCE.registerModelProvider(NMExtraModels.EXTRA_MODELS);
        NMParticles.Client.init();

        // BlockEntity renderers
        BlockEntityRendererRegistry.register(NMBlockEntities.GLASS_TANK_BLOCK_ENTITY, GlassTankRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_GLASS_TANK_LAYER, GlassTankModel::getTexturedModelData);
        BlockEntityRendererRegistry.register(NMBlockEntities.FLUID_BUFFER, FluidBufferRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.ITEM_BUFFER_BLOCK_ENTITY, DisplayPlatformRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.TROMMEL, TrommelRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.SMALL_TROMMEL, SmallTrommelRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.INTEGRATOR, IntegratorEggRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.BIG_LEVER, BigLeverRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.PNEUMATIC_PIPE, PneumaticPipeRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.MERGE_ITEM_PIPE, MergePipeRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.ITEM_PUMP, ItemPumpRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.EJECTOR, EjectorRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.CONVERTER, ConverterRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.LINEAR_OSCILLATOR, LinearOscillatorRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.MOTOR, MotorRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.DEPLOYER, DeployerRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.AGITATOR, AgitatorRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.VAT_CONTROLLER, VatRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.MIXER, MixerRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.MULTI_TANK, MultiTankRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.GRINDER, GrinderRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.STIRLING_ENGINE, StirlingEngineRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.CRUCIBLE, CrucibleRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.ALEMBIC, AlembicRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.CASTING_BASIN, CastingBasinRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.HYDRAULIC_PRESS, HydraulicPressRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.PEDESTAL, PedestalRenderer::new);

        EntityRendererRegistry.register(NMEntities.TANK_MINECART, (ctx) -> new TankMinecartRenderer(ctx, TANK_MINECART));
        EntityModelLayerRegistry.registerModelLayer(TANK_MINECART, MinecartEntityModel::getTexturedModelData);

        GeoItemRenderer.registerItemRenderer(NMItems.SLASHER, new SwordRenderer<>(new GenericModel<>(
                NeepMeat.NAMESPACE,
                "geo/slasher.geo.json",
                "textures/item/slasher.png",
                "animations/slasher.animation.json"
        )));

        GeoItemRenderer.registerItemRenderer(NMItems.CHEESE_CLEAVER, new SwordRenderer<>(new GenericModel<>(
                NeepMeat.NAMESPACE,
                "geo/cheese_cleaver.geo.json",
                "textures/item/cheese_cleaver.png",
                "animations/cheese_cleaver.animation.json"
        )));

//        GeoArmorRenderer.registerArmorRenderer(new MeatSteelArmourRenderer(new GenericModel<>(
//                NeepMeat.NAMESPACE,
//                "geo/meat_steel_armour.geo.json",
//                "textures/entity/armour/meat_steel_armour.png",
//                "animations/meat_steel_armour.animation.json"
//        )),
//                NMItems.MEAT_STEEL_BOOTS, NMItems.MEAT_STEEL_LEGS, NMItems.MEAT_STEEL_CHESTPLATE);

        // Fluid textures
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_BLOOD, NMFluids.FLOWING_BLOOD, new SimpleFluidRenderHandler(
                BLOOD,
                BLOOD_FLOWING
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_PATINA_TREATMENT, NMFluids.FLOWING_PATINA_TREATMENT, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0x4db99a
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_WORK_FLUID, NMFluids.FLOWING_WORK_FLUID, new SimpleFluidRenderHandler(
                WORK_FLUID,
                WORK_FLUID_FLOWING,
                0x999999
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_CHARGED_WORK_FLUID, NMFluids.FLOWING_CHARGED_WORK_FLUID, new SimpleFluidRenderHandler(
                CHARGED_WORK_FLUID,
                CHARGED_WORK_FLUID_FLOWING,
                0xFFFFFF
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_ETHEREAL_FUEL, NMFluids.FLOWING_ETHEREAL_FUEL, new SimpleFluidRenderHandler(
                ETHEREAL_FUEL,
                ETHEREAL_FUEL_FLOWING,
                0xFFFFFF
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_ELDRITCH_ENZYMES, NMFluids.FLOWING_ELDRITCH_ENZYMES, new SimpleFluidRenderHandler(
                ELDRITCH_ENZYMES,
                ELDRITCH_ENZYMES_FLOWING,
                0x3657a2
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_DIRTY_ORE_FAT, NMFluids.FLOWING_DIRTY_ORE_FAT, new SimpleFluidRenderHandler(
                DIRTY_ORE_FAT,
                DIRTY_ORE_FAT_FLOWING,
                0x3657a2
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_CLEAN_ORE_FAT, NMFluids.FLOWING_CLEAN_ORE_FAT, new SimpleFluidRenderHandler(
                CLEAN_ORE_FAT,
                CLEAN_ORE_FAT_FLOWING,
                0x3657a2
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_MEAT, NMFluids.FLOWING_MEAT, new SimpleFluidRenderHandler(
                MEAT,
                MEAT_FLOWING
        ));

        FluidVariantRendering.register(NMFluids.STILL_DIRTY_ORE_FAT, new OreFatFluidVariantRenderHandler());
        FluidVariantRendering.register(NMFluids.FLOWING_DIRTY_ORE_FAT, new OreFatFluidVariantRenderHandler());
        FluidVariantAttributes.register(NMFluids.STILL_DIRTY_ORE_FAT, new OreFatAttributeHandler());
        FluidVariantAttributes.register(NMFluids.FLOWING_DIRTY_ORE_FAT, new OreFatAttributeHandler());

        FluidVariantRendering.register(NMFluids.STILL_CLEAN_ORE_FAT, new OreFatFluidVariantRenderHandler());
        FluidVariantRendering.register(NMFluids.FLOWING_CLEAN_ORE_FAT, new OreFatFluidVariantRenderHandler());
        FluidVariantAttributes.register(NMFluids.STILL_CLEAN_ORE_FAT, new OreFatAttributeHandler());
        FluidVariantAttributes.register(NMFluids.FLOWING_CLEAN_ORE_FAT, new OreFatAttributeHandler());

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) ->
        {
            registry.register(WORK_FLUID);
            registry.register(WORK_FLUID_FLOWING);
            registry.register(CHARGED_WORK_FLUID);
            registry.register(CHARGED_WORK_FLUID_FLOWING);
            registry.register(ETHEREAL_FUEL);
            registry.register(ETHEREAL_FUEL_FLOWING);
            registry.register(ELDRITCH_ENZYMES);
            registry.register(ELDRITCH_ENZYMES_FLOWING);
            registry.register(DIRTY_ORE_FAT);
            registry.register(DIRTY_ORE_FAT_FLOWING);
            registry.register(CLEAN_ORE_FAT);
            registry.register(CLEAN_ORE_FAT_FLOWING);
            registry.register(BLOOD);
            registry.register(BLOOD_FLOWING);
            registry.register(MEAT);
            registry.register(MEAT_FLOWING);
        });


        // Coloured blocks
        for (BasePaintedBlock.PaintedBlock block : BasePaintedBlock.COLOURED_BLOCKS)
        {
            ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> block.getRawCol(), block);
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> block.getRawCol(), block.asItem());
        }
    }

    public static void registerScreens()
    {
        HandledScreens.register(ScreenHandlerInit.BUFFER_SCREEN_HANDLER, BufferScreen::new);
        HandledScreens.register(ScreenHandlerInit.CONTENT_DETECTOR_SCREEN_HANDLER, ContentDetectorScreen::new);
        HandledScreens.register(ScreenHandlerInit.ROUTER, RouterScreen::new);
        HandledScreens.register(ScreenHandlerInit.STIRLING_ENGINE, StirlingEngineScreen::new);
        HandledScreens.register(ScreenHandlerInit.ALLOY_KILN, AlloyKilnScreen::new);
    }

    public static void registerLayers()
    {
        // Fluids
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_BLOOD, NMFluids.FLOWING_BLOOD);
//        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_ENRICHED_BLOOD, NMFluids.FLOWING_ENRICHED_BLOOD);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_ELDRITCH_ENZYMES, NMFluids.FLOWING_ELDRITCH_ENZYMES);

        // Other blocks
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.GLASS_TANK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MULTI_TANK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.FLUID_BUFFER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MESH_PANE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.RUSTED_BARS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.PNEUMATIC_TUBE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MERGE_ITEM_PIPE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.ITEM_PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.CONVERTER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.DEPLOYER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MIXER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.LEADED_GLASS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.ALEMBIC);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.TRANSDUCER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SMALL_TROMMEL);
//        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.CAUTION_TAPE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.BLOOD_BUBBLE_SAPLING);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.BLOOD_BUBBLE_LEAVES);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.BLOOD_BUBBLE_LEAVES_FLOWERING);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.WHISPER_WHEAT);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.FLESH_POTATO);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SCAFFOLD_PLATFORM);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.SCAFFOLD_PLATFORM.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.SCAFFOLD_PLATFORM.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.BLUE_SCAFFOLD);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.BLUE_SCAFFOLD.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.BLUE_SCAFFOLD.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.YELLOW_SCAFFOLD);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.YELLOW_SCAFFOLD.stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) NMBlocks.YELLOW_SCAFFOLD.slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.VAT_WINDOW);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SCAFFOLD_TRAPDOOR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), NMBlocks.SCAFFOLD_TRAPDOOR);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.FLUID_DRAIN);

//        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SLOPE_TEST);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MIXER_TOP);
    }
}
