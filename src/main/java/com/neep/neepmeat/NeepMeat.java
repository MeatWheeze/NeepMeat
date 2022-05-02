package com.neep.neepmeat;

import com.neep.meatlib.item.NMItem;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.init.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

public class NeepMeat implements ModInitializer, ClientModInitializer
{
	public static final String NAMESPACE = "neepmeat";
	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

	@Override
	public void onInitialize()
	{
		LOGGER.info("Hello from NeepMeat!");

		GeckoLib.initialize();

		// Oooh, the jank! There must be a better way.
		NMBlocks blocks = new NMBlocks();
		NMItems items = new NMItems();

		BlockRegistry.registerBlocks();
		NMFluids.initialiseFluids();
		ItemRegistry.registerItems();
		SoundInitialiser.registerSounds();
		NMBlockEntities.initialiseBlockEntities();
		ScreenHandlerInit.registerScreenHandlers();

		FluidNetwork.registerEvents();
	}

	@Override
	public void onInitializeClient()
	{
		NeepMeatClient.registerRenderers();
	}
}
