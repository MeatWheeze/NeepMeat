package com.neep.neepmeat;

import com.neep.meatlib.MeatLib;
import com.neep.neepmeat.api.Burner;
import com.neep.neepmeat.api.enlightenment.EnlightenmentEvent;
import com.neep.neepmeat.api.enlightenment.EnlightenmentEventManager;
import com.neep.neepmeat.api.enlightenment.EnlightenmentUtil;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.block.entity.FurnaceBurnerImpl;
import com.neep.neepmeat.datagen.NMRecipeGenerator;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.enlightenment.LimbEnlightenmentEvent;
import com.neep.neepmeat.entity.effect.NMStatusEffects;
import com.neep.neepmeat.entity.worm.WormActions;
import com.neep.neepmeat.guide.GuideReloadListener;
import com.neep.neepmeat.implant.player.*;
import com.neep.neepmeat.init.*;
import com.neep.neepmeat.machine.charnel_compactor.CharnelCompactorStorage;
import com.neep.neepmeat.machine.homogeniser.MeatAdditives;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.machine.synthesiser.MobSynthesisRegistry;
import com.neep.neepmeat.network.MachineDiagnosticsRequest;
import com.neep.neepmeat.network.NMTrackedData;
import com.neep.neepmeat.network.ToolTransformPacket;
import com.neep.neepmeat.plc.PLCBlocks;
import com.neep.neepmeat.plc.recipe.PLCRecipes;
import com.neep.neepmeat.potion.NMPotions;
import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.blood_network.BloodNetworkManager;
import com.neep.neepmeat.transport.fluid_network.FluidNodeManager;
import com.neep.neepmeat.util.Bezier;
import com.neep.neepmeat.world.NMFeatures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

public class NeepMeat implements ModInitializer
{
	public static final String NAMESPACE = "neepmeat";
	public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

//	public static final String disableGeckoExamples = System.setProperty(GeckoLibMod.DISABLE_EXAMPLES_PROPERTY_KEY, "true");

	@Override
	public void onInitialize()
	{
		try (var mcontext = MeatLib.getContext(NAMESPACE))
		{
			LOGGER.info("Hello from NeepMeat!");
			new Bezier();

			GeckoLib.initialize();

			NMrecipeTypes.init();
			NMRecipeGenerator.init();
			new NMBlocks();
			new NMItems();
			NMLootTables.init();
			NMTags.init();
			NMParticles.init();
			new NMSounds();

			PLCBlocks.init();

			NMFluids.initialise();
			NMBlockEntities.initialise();
			NMEntities.initialise();
			OreFatRegistry.init();
			NMStatusEffects.init();
			NMPotions.init();
			NMGraphicsEffects.init();

			// --- Transport module ---
			ItemTransport.init();
			FluidTransport.init();
			BloodNetworkManager.init();


//		EnlightenmentUtil.init();
//		EnlightenmentEventManager.init();


			// --- Other misc things ---
			ToolTransformPacket.registerReceiver();
			MachineDiagnosticsRequest.registerReceiver();
			MotorisedBlock.DiagnosticsProvider.init();

			NMTrackedData.init();

			NMFeatures.init();


			ItemStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> CharnelCompactorStorage.getStorage(world, pos, direction), NMBlocks.CHARNEL_COMPACTOR);
			FluidStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> blockEntity instanceof IntegratorBlockEntity be ? be.getStorage(world, pos, state, direction) : null, NMBlocks.INTEGRATOR_EGG);

			Burner.LOOKUP.registerForBlockEntity(FurnaceBurnerImpl::get, BlockEntityType.FURNACE);
			Burner.LOOKUP.registerForBlocks((world, pos, state, blockEntity, context) -> () -> 20, Blocks.LAVA, Blocks.LAVA_CAULDRON, Blocks.MAGMA_BLOCK);

			PLCRecipes.init();

			ScreenHandlerInit.registerScreenHandlers();

			// Fluid transfer things
			FluidNodeManager.registerEvents();

			// Meat additives
			MeatAdditives.init();

			// Resource reload listeners
			ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(GuideReloadListener.getInstance());
			ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MobSynthesisRegistry.getInstance());

			WormActions.init();

			PlayerImplantManager.init();

//			PlayerAttachmentManager.registerAttachment(PlayerImplantManager.ID, PlayerImplantManager::new);
//			Registry.register(PlayerImplantRegistry.REGISTRY, ExtraMouthImplant.ID, ExtraMouthImplant::new);
//			Registry.register(PlayerImplantRegistry.REGISTRY, SkeltalImplant.ID, SkeltalImplant::new);

			Registry.register(ImplantRegistry.REGISTRY, ExtraKneeImplant.ID, ExtraKneeImplant::new);
			Registry.register(ImplantRegistry.REGISTRY, PinealEyeImplant.ID, PinealEyeImplant::new);
			Registry.register(ImplantRegistry.REGISTRY, ExtraMouthImplant.ID, ExtraMouthImplant::new);
			Registry.register(ImplantRegistry.REGISTRY, LungExtensionsImplant.ID, LungExtensionsImplant::new);

			Registry.register(EntityImplantInstaller.REGISTRY, PinealEyeImplant.ID, NMItems.PINEAL_EYE);
			Registry.register(EntityImplantInstaller.REGISTRY, ExtraKneeImplant.ID, NMItems.EXTRA_KNEES);
			Registry.register(EntityImplantInstaller.REGISTRY, ExtraMouthImplant.ID, NMItems.EXTRA_MOUTH);
			Registry.register(EntityImplantInstaller.REGISTRY, LungExtensionsImplant.ID, NMItems.LUNG_EXTENSIONS);
			Registry.register(EntityImplantInstaller.REGISTRY, new Identifier(NeepMeat.NAMESPACE, "chrysalis"), NMItems.CHRYSALIS);

			Registry.register(EnlightenmentEventManager.EVENTS, new Identifier(NAMESPACE, "limb_spawn"), new EnlightenmentEvent.SimpleFactory(LimbEnlightenmentEvent::new));

			NMCommonNetwork.init();

			EnlightenmentEventManager.init();
			EnlightenmentUtil.init();
		}
	}

	public static void cowThingy(CowEntity cow, PlayerEntity player, Hand hand)
	{
		ItemStack stack = player.getStackInHand(hand);
		if (stack.isOf(NMItems.ENLIGHTENED_BRAIN))
		{
			var random = cow.getRandom();
			int count = random.nextBetween(2, 4);

			if (!player.isCreative())
			{
				stack.decrement(1);
			}

			for (int i = 0; i < count; ++i)
			{
				cow.dropStack(new ItemStack(NMItems.ROUGH_BRAIN), 0.5f);
			}
			player.world.playSound(cow.getX(), cow.getY(), cow.getZ(),
					SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.NEUTRAL, 1, 1, false);
			cow.setDropsLoot(false);
			cow.kill();
		}
	}
}
