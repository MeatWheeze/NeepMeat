package com.neep.neepmeat;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.block.machine.CharnelCompactorStorage;
import com.neep.neepmeat.blockentity.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.blockentity.integrator.IntegratorStorage;
import com.neep.neepmeat.datagen.NMRecipes;
import com.neep.neepmeat.fluid_transfer.FluidNetwork;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.datagen.tag.NMTags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

public class NeepMeat implements ModInitializer
{
	public static final String NAMESPACE = "neepmeat";
	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

	@Override
	public void onInitialize()
	{
		LOGGER.info("Hello from NeepMeat!");

		GeckoLib.initialize();

		// Oooh, the jank! There must be a better way.
		MeatLib.setNamespace(NAMESPACE);
		NMrecipeTypes.init();
		NMRecipes.init();
		NMBlocks blocks = new NMBlocks();
		NMItems items = new NMItems();
		NMLootTables.init();
		NMTags.init();
		NMParticles.init();

		NMFluids.initialise();
		SoundInitialiser.initialise();
		NMBlockEntities.initialise();
		NMEntities.initialise();

		ItemStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> CharnelCompactorStorage.getStorage(world, pos, direction), NMBlocks.CHARNEL_COMPACTOR);
		FluidStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> blockEntity instanceof IntegratorBlockEntity be ? be.getStorage(world, pos, state, direction) : null, NMBlocks.INTEGRATOR_EGG);

		ScreenHandlerInit.registerScreenHandlers();

		FluidNetwork.registerEvents();
	}
}
