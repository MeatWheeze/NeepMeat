package com.neep.neepmeat;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.attachment.player.PlayerAttachmentManager;
import com.neep.neepmeat.api.Burner;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.player.upgrade.ExtraMouthUpgrade;
import com.neep.neepmeat.player.upgrade.PlayerUpgradeManager;
import com.neep.neepmeat.block.entity.FurnaceBurnerImpl;
import com.neep.neepmeat.entity.effect.NMStatusEffects;
import com.neep.neepmeat.guide.GuideReloadListener;
import com.neep.neepmeat.datagen.NMRecipes;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.machine.charnel_compactor.CharnelCompactorStorage;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.machine.synthesiser.MobSynthesisRegistry;
import com.neep.neepmeat.network.ToolTransformPacket;
import com.neep.neepmeat.player.upgrade.PlayerUpgradeRegistry;
import com.neep.neepmeat.player.upgrade.ExtraKneeUpgrade;
import com.neep.neepmeat.potion.NMPotions;
import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.data.PipeNetworkSerialiser;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.transport.fluid_network.StagedTransactions;
import com.neep.neepmeat.world.NMFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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
		LOGGER.info("Hello from NeepMeat!");

		GeckoLib.initialize();

		// Oooh, the jank! There must be a better way.
		MeatLib.setNamespace(NAMESPACE);
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


//		EnlightenmentUtil.init();
//		EnlightenmentEventManager.init();


		// --- Other misc things ---
		ToolTransformPacket.registerReceiver();

		NMFeatures.init();

		ItemStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> CharnelCompactorStorage.getStorage(world, pos, direction), NMBlocks.CHARNEL_COMPACTOR);
		FluidStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> blockEntity instanceof IntegratorBlockEntity be ? be.getStorage(world, pos, state, direction) : null, NMBlocks.INTEGRATOR_EGG);

		Burner.LOOKUP.registerForBlockEntity(FurnaceBurnerImpl::get, BlockEntityType.FURNACE);
		Burner.LOOKUP.registerForBlocks((world, pos, state, blockEntity, context) -> () -> 20, Blocks.LAVA, Blocks.LAVA_CAULDRON, Blocks.MAGMA_BLOCK);

		ScreenHandlerInit.registerScreenHandlers();

		FluidNodeManager.registerEvents();
		PipeNetworkSerialiser.init();
		StagedTransactions.init();

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(GuideReloadListener.getInstance());


		// Register all blocks and items
		MeatLib.flush();

		PlayerAttachmentManager.registerAttachment(PlayerUpgradeManager.ID, PlayerUpgradeManager::new);
		Registry.register(PlayerUpgradeRegistry.REGISTRY, new Identifier(NeepMeat.NAMESPACE, "extra_mouth"), ExtraMouthUpgrade::new);
		Registry.register(PlayerUpgradeRegistry.REGISTRY, new Identifier(NeepMeat.NAMESPACE, "extra_knee"), ExtraKneeUpgrade::new);
	}
}
