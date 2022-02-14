package com.neep.neepmeat;

import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import com.neep.neepmeat.init.BlockInitialiser;
import com.neep.neepmeat.init.ItemInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NeepMeat implements ModInitializer, ClientModInitializer
{

	public static final String NAMESPACE = "neepmeat";
	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

	@Override
	public void onInitialize()
	{

		LOGGER.info("Hello Fabric world!");

		BlockInitialiser.registerBlocks();
		ItemInit.registerItems();
		BlockEntityInitialiser.initialiseBlockEntities();
	}

	@Override
	public void onInitializeClient()
	{
		NeepMeatClient.registerRenderers();
	}
}
