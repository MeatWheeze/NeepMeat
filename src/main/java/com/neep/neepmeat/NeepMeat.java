package com.neep.neepmeat;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.attachment.player.PlayerAttachmentManager;
import com.neep.neepmeat.api.Burner;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.block.entity.FurnaceBurnerImpl;
import com.neep.neepmeat.datagen.NMRecipes;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.entity.effect.NMStatusEffects;
import com.neep.neepmeat.entity.worm.WormActions;
import com.neep.neepmeat.guide.GuideReloadListener;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.machine.charnel_compactor.CharnelCompactorStorage;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.machine.synthesiser.MobSynthesisRegistry;
import com.neep.neepmeat.network.NMTrackedData;
import com.neep.neepmeat.network.ToolTransformPacket;
import com.neep.neepmeat.player.upgrade.*;
import com.neep.neepmeat.potion.NMPotions;
import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import com.neep.neepmeat.transport.blood_network.BloodNetworkManager;
import com.neep.neepmeat.transport.data.PipeNetworkSerialiser;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.util.Bezier;
import com.neep.neepmeat.world.NMFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

public class NeepMeat implements ModInitializer
{
	public static final String NAMESPACE = "neepmeat";
	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

	public static final String disableGeckoExamples = System.setProperty(GeckoLibMod.DISABLE_EXAMPLES_PROPERTY_KEY, "true");

	@Override
	public void onInitialize()
	{
		try (var mcontext = MeatLib.getContext(NAMESPACE))
		{
			LOGGER.info("Hello from NeepMeat!");
			new Bezier();

			GeckoLib.initialize();

			NMrecipeTypes.init();
			NMRecipes.init();
			new NMBlocks();
			new NMItems();
			NMLootTables.init();
			NMTags.init();
			NMParticles.init();
			new NMSounds();

			NMFluids.initialise();
			NMBlockEntities.initialise();
			NMEntities.initialise();
			OreFatRegistry.init();
			NMStatusEffects.init();
			NMPotions.init();
			MobSynthesisRegistry.initDefaults();
			NMGraphicsEffects.init();

			// --- Transport module ---
			ItemTransport.init();
			FluidTransport.init();
			BloodNetworkManager.init();


//		EnlightenmentUtil.init();
//		EnlightenmentEventManager.init();


			// --- Other misc things ---
			ToolTransformPacket.registerReceiver();

			NMTrackedData.init();

			NMFeatures.init();

			ItemStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> CharnelCompactorStorage.getStorage(world, pos, direction), NMBlocks.CHARNEL_COMPACTOR);
			FluidStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> blockEntity instanceof IntegratorBlockEntity be ? be.getStorage(world, pos, state, direction) : null, NMBlocks.INTEGRATOR_EGG);

			Burner.LOOKUP.registerForBlockEntity(FurnaceBurnerImpl::get, BlockEntityType.FURNACE);
			Burner.LOOKUP.registerForBlocks((world, pos, state, blockEntity, context) -> () -> 20, Blocks.LAVA, Blocks.LAVA_CAULDRON, Blocks.MAGMA_BLOCK);

			ScreenHandlerInit.registerScreenHandlers();

			// Fluid transfer things
			FluidNodeManager.registerEvents();
			PipeNetworkSerialiser.init();

			// Guide resources
			ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(GuideReloadListener.getInstance());

			WormActions.init();

			PlayerUpgradeManager.init();

			PlayerAttachmentManager.registerAttachment(PlayerUpgradeManager.ID, PlayerUpgradeManager::new);
			Registry.register(PlayerUpgradeRegistry.REGISTRY, ExtraMouthUpgrade.ID, ExtraMouthUpgrade::new);
			Registry.register(PlayerUpgradeRegistry.REGISTRY, ExtraKneeUpgrade.ID, ExtraKneeUpgrade::new);
			Registry.register(PlayerUpgradeRegistry.REGISTRY, SkeltalUpgrade.ID, SkeltalUpgrade::new);
		}
	}
}
