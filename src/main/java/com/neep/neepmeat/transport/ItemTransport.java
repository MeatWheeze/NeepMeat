package com.neep.neepmeat.transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.entity.StorageBusBlockEntity;
import com.neep.neepmeat.transport.screen_handler.ItemRequesterScreenHandler;
import com.neep.neepmeat.transport.api.item_network.RoutablePipe;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.block.item_transport.ItemRequesterBlock;
import com.neep.neepmeat.transport.block.item_transport.PipeDriverBlock;
import com.neep.neepmeat.transport.block.item_transport.StorageBusBlock;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ItemTransport
{
    public static final int BFS_MAX_DEPTH = 800;

    public static BlockApiLookup<IItemPipe, Direction> ITEM_PIPE = BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "item_pipe"), IItemPipe.class, Direction.class);

    public static BlockEntityType<PipeDriverBlock.PDBlockEntity> PIPE_DRIVER_BE;
    public static BlockEntityType<StorageBusBlockEntity> STORAGE_BUS_BE;
    public static BlockEntityType<ItemRequesterBlockEntity> ITEM_REQUESTER_BE;

    public static final Block PIPE_DRIVER = BlockRegistry.queue(new PipeDriverBlock("pipe_driver", FabricBlockSettings.of(Material.METAL).hardness(0.3f).sounds(BlockSoundGroup.METAL)));
    public static final Block STORAGE_BUS = BlockRegistry.queue(new StorageBusBlock("storage_bus", ItemSettings.block(), FabricBlockSettings.of(Material.METAL).hardness(0.3f).sounds(BlockSoundGroup.METAL)));
    public static final Block ITEM_REQUESTER = BlockRegistry.queue(new ItemRequesterBlock("item_requester", ItemSettings.block(), FabricBlockSettings.of(Material.METAL).hardness(0.3f).sounds(BlockSoundGroup.METAL)));

    public static ScreenHandlerType<ItemRequesterScreenHandler> ITEM_REQUESTER_HANDLER = new ScreenHandlerType<>(ItemRequesterScreenHandler::new);

    public static void init()
    {
        PIPE_DRIVER_BE = NMBlockEntities.registerBlockEntity("pipe_driver", PipeDriverBlock.PDBlockEntity::new, PIPE_DRIVER);
        RoutingNetwork.LOOKUP.registerForBlockEntity(PipeDriverBlock.PDBlockEntity::getNetwork, PIPE_DRIVER_BE);
        STORAGE_BUS_BE = NMBlockEntities.registerBlockEntity("storage_bus", StorageBusBlockEntity::new, STORAGE_BUS);
        RoutablePipe.LOOKUP.registerSelf(STORAGE_BUS_BE);
        ITEM_REQUESTER_BE = NMBlockEntities.registerBlockEntity("item_requester", ItemRequesterBlockEntity::new, ITEM_REQUESTER);
        RoutablePipe.LOOKUP.registerSelf(ITEM_REQUESTER_BE);

        ITEM_PIPE.registerFallback((world, pos, state, blockEntity, context) -> state.getBlock() instanceof IItemPipe pipe ? pipe : null);

        ITEM_REQUESTER_HANDLER = ScreenHandlerInit.register(NeepMeat.NAMESPACE, "item_requester", ItemRequesterScreenHandler::new);
    }

    public static void propagateUpdate(BlockPos pos, ServerWorld world)
    {

    }
}
