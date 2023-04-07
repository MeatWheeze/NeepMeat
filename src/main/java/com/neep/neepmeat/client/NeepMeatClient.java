package com.neep.neepmeat.client;

import com.neep.meatlib.block.BaseBuildingBlock;
import com.neep.meatlib.block.BasePaintedBlock;
import com.neep.meatlib.graphics.client.GraphicsEffectClient;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.effect.ReminaGraphicsEvent;
import com.neep.neepmeat.client.fluid.NMFluidsClient;
import com.neep.neepmeat.client.hud.HUDOverlays;
import com.neep.neepmeat.client.model.GlassTankModel;
import com.neep.neepmeat.client.model.GlomeEntityModel;
import com.neep.neepmeat.client.renderer.*;
import com.neep.neepmeat.client.screen.*;
import com.neep.neepmeat.client.screen.tablet.GuideScreen;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.machine.assembler.AssemblerRenderer;
import com.neep.neepmeat.machine.bottler.BottlerRenderer;
import com.neep.neepmeat.machine.casting_basin.CastingBasinRenderer;
import com.neep.neepmeat.machine.crucible.AlembicRenderer;
import com.neep.neepmeat.machine.crucible.CrucibleRenderer;
import com.neep.neepmeat.machine.death_blades.DeathBladesRenderer;
import com.neep.neepmeat.machine.grinder.GrinderRenderer;
import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressRenderer;
import com.neep.neepmeat.machine.item_mincer.ItemMincerRenderer;
import com.neep.neepmeat.machine.mixer.MixerRenderer;
import com.neep.neepmeat.machine.motor.MotorRenderer;
import com.neep.neepmeat.machine.multitank.MultiTankRenderer;
import com.neep.neepmeat.machine.pylon.PylonRenderer;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelRenderer;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineRenderer;
import com.neep.neepmeat.machine.surgical_controller.TableControllerRenderer;
import com.neep.neepmeat.machine.synthesiser.SynthesiserRenderer;
import com.neep.neepmeat.machine.trough.TroughRenderer;
import com.neep.neepmeat.network.BlockSoundPacket;
import com.neep.neepmeat.network.ParticleSpawnPacket;
import com.neep.neepmeat.network.TankMessagePacket;
import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.block.fluid_transport.FilterPipeBlock;
import com.neep.neepmeat.transport.client.TransportClient;
import com.neep.neepmeat.transport.client.renderer.WindowPipeRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value= EnvType.CLIENT)
public class NeepMeatClient implements ClientModInitializer
{
    public static final EntityModelLayer MODEL_GLASS_TANK_LAYER = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "glass_tank"), "main");
    public static final EntityModelLayer TANK_MINECART = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "tank_minecart"), "main");
    public static final EntityModelLayer GLOME = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "glome"), "main");
    public static final EntityModelLayer EGG = new EntityModelLayer(new Identifier(NeepMeat.NAMESPACE, "egg"), "main");

    @Override
    public void onInitializeClient()
    {
        registerRenderers();
        registerLayers();
        registerScreens();
        NMFluidsClient.registerFluidRenderers();
        HUDOverlays.init();

        TransportClient.init();

        TankMessagePacket.Client.registerReceiver();
        ParticleSpawnPacket.Client.registerReceiver();
        BlockSoundPacket.Client.registerReceiver();
        NMKeys.registerKeybindings();

        GraphicsEffectClient.registerEffect(NMGraphicsEffects.REMINA, ReminaGraphicsEvent::new);
    }

    public static void registerRenderers()
    {
        EntityModelLayerRegistry.registerModelLayer(GLOME, GlomeEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(EGG, GlomeEntityModel::getTexturedModelData);

        // Custom baked models
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> new NeepMeatModelProvider());
        ModelLoadingRegistry.INSTANCE.registerModelProvider(NMExtraModels.EXTRA_MODELS);
        NMParticles.Client.init();

        // BlockEntity renderers
        BlockEntityRendererRegistry.register(NMBlockEntities.WINDOW_PIPE, WindowPipeRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.GLASS_TANK_BLOCK_ENTITY, GlassTankRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MODEL_GLASS_TANK_LAYER, GlassTankModel::getTexturedModelData);
        BlockEntityRendererRegistry.register(NMBlockEntities.FLUID_BUFFER, FluidBufferRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.ITEM_BUFFER_BLOCK_ENTITY, c -> new ItemBlockEntityRenderer<>(
                c, be -> be.getStorage(null).getAsStack(), be ->
        {
            be.stackRenderDelta = MathHelper.lerp(0.1f, be.stackRenderDelta, be.getStorage(null).getAmount() <= 0 ? 0.3f : 0f);
            return 0.25f + be.stackRenderDelta;
        }));
        BlockEntityRendererRegistry.register(NMBlockEntities.TROMMEL, TrommelRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.SMALL_TROMMEL, SmallTrommelRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.INTEGRATOR, IntegratorEggRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.BIG_LEVER, BigLeverRenderer::new);
        BlockEntityRendererRegistry.register(NMBlockEntities.PNEUMATIC_PIPE, ItemPipeRenderer::new);
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

        BlockEntityRendererRegistry.register(NMBlockEntities.PEDESTAL, c -> new ItemBlockEntityRenderer<>(
                c, be -> be.getStorage(null).getResource().toStack(), be -> 0.67f));
        BlockEntityRendererRegistry.register(NMBlockEntities.ASSEMBLER, AssemblerRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.DEATH_BLADES, DeathBladesRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.FEEDING_TROUGH, TroughRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.BOTTLER, BottlerRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.PYLON, PylonRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.SYNTHESISER, SynthesiserRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.TABLE_CONTROLLER, TableControllerRenderer::new);

        BlockEntityRendererRegistry.register(NMBlockEntities.ITEM_MINCER, ItemMincerRenderer::new);

        EntityRendererRegistry.register(NMEntities.TANK_MINECART, ctx -> new TankMinecartRenderer(ctx, TANK_MINECART));
        EntityRendererRegistry.register(NMEntities.GLOME, ctx -> new GlomeEntityRenderer(ctx, GLOME));
        EntityRendererRegistry.register(NMEntities.EGG, ctx -> new EggEntityRenderer(ctx, EGG));

        EntityModelLayerRegistry.registerModelLayer(TANK_MINECART, MinecartEntityModel::getTexturedModelData);

        // Coloured blocks
        for (BasePaintedBlock.PaintedBlock block : BasePaintedBlock.COLOURED_BLOCKS)
        {
            ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> block.getRawCol(), block);
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> block.getRawCol(), block.asItem());
        }
        ColorProviderRegistry.BLOCK.register(FilterPipeBlock::getTint, FluidTransport.FILTER_PIPE);
    }

    public static void registerScreens()
    {
        HandledScreens.register(ScreenHandlerInit.BUFFER_SCREEN_HANDLER, BufferScreen::new);
        HandledScreens.register(ScreenHandlerInit.CONTENT_DETECTOR_SCREEN_HANDLER, ContentDetectorScreen::new);
        HandledScreens.register(ScreenHandlerInit.ROUTER, RouterScreen::new);
        HandledScreens.register(ScreenHandlerInit.STIRLING_ENGINE, StirlingEngineScreen::new);
        HandledScreens.register(ScreenHandlerInit.ALLOY_KILN, AlloyKilnScreen::new);
        HandledScreens.register(ScreenHandlerInit.ASSEMBLER, AssemblerScreen::new);
        HandledScreens.register(ScreenHandlerInit.WORKSTATION, WorkstationScreen::new);
        HandledScreens.register(ScreenHandlerInit.GUIDE, GuideScreen::new);
        HandledScreens.register(ScreenHandlerInit.FLUID_RATIONER, FluidRationerScreen::new);
    }

    public static void registerLayers()
    {
        // Fluids
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_BLOOD, NMFluids.FLOWING_BLOOD);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_PATINA_TREATMENT, NMFluids.FLOWING_PATINA_TREATMENT);
//        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_ENRICHED_BLOOD, NMFluids.FLOWING_ENRICHED_BLOOD);
        BlockRenderLayerMap.INSTANCE.putFluids(RenderLayer.getTranslucent(), NMFluids.STILL_ELDRITCH_ENZYMES, NMFluids.FLOWING_ELDRITCH_ENZYMES);

        // Other blocks
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), FluidTransport.GLASS_TANK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MULTI_TANK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.FLUID_BUFFER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MESH_PANE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), FluidTransport.PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.RUSTED_BARS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.PNEUMATIC_TUBE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MERGE_ITEM_PIPE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.ITEM_PUMP);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.CONVERTER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.DEPLOYER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MIXER);
//        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.LEADED_GLASS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.COLLECTOR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.TRANSDUCER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SMALL_TROMMEL);
//        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.CAUTION_TAPE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), NMBlocks.ASSEMBLER);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.BOTTLER);
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

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.REINFORCED_GLASS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) ((BaseBuildingBlock) NMBlocks.REINFORCED_GLASS).stairs);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), (Block) ((BaseBuildingBlock) NMBlocks.REINFORCED_GLASS).slab);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SCAFFOLD_TRAPDOOR);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), NMBlocks.SCAFFOLD_TRAPDOOR);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.FLUID_DRAIN);

//        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.SLOPE_TEST);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), NMBlocks.MIXER_TOP);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), FluidTransport.WINDOW_PIPE);
    }
}
