package com.neepmeat.realistic_fluid;

import com.neepmeat.realistic_fluid.block.BlockInitialiser;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealisticFluid implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String NAMESPACE = "realistic_fluids";
	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");
		BlockInitialiser.registerBlocks();
	}
}
