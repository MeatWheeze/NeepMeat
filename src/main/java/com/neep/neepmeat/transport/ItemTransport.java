package com.neep.neepmeat.transport;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.meatlib.registry.annotation.RegisterMe;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.machine.dumper.DumperBlock;
import com.neep.neepmeat.transport.api.item_network.RoutablePipe;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.block.item_transport.*;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity;
import com.neep.neepmeat.transport.block.item_transport.entity.StorageBusBlockEntity;
import com.neep.neepmeat.transport.machine.item.*;
import com.neep.neepmeat.transport.screen_handler.ItemRequesterScreenHandler;
import com.neep.neepmeat.transport.screen_handler.TransportScreenHandlers;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.minecraft.registry.tag.BlockTags.AXE_MINEABLE;

@RegisterMe(NeepMeat.NAMESPACE)
public class ItemTransport
{
    private static final RegistrationContext C = NeepMeat.C;

    public static final int BFS_MAX_DEPTH = 800;

    public static BlockApiLookup<ItemPipe, Direction> ITEM_PIPE_LOOKUP = BlockApiLookup.get(new Identifier(NeepMeat.NAMESPACE, "item_pipe"), ItemPipe.class, Direction.class);
    public static BlockEntityType<PipeDriverBlock.PipeDriverBlockEntity> PIPE_DRIVER_BE;
    public static BlockEntityType<StorageBusBlockEntity> STORAGE_BUS_BE;
    public static BlockEntityType<ItemRequesterBlockEntity> ITEM_REQUESTER_BE;

    public static final Block PIPE_DRIVER = new PipeDriverBlock(C, ItemSettings.block().tooltip(TooltipSupplier.hidden(2)).plcActuator(), FabricBlockSettings.create().hardness(0.3f).sounds(BlockSoundGroup.METAL));
    public static final Block STORAGE_BUS = new StorageBusBlock(C, ItemSettings.block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.create().hardness(0.3f).sounds(BlockSoundGroup.METAL));
    public static final Block ITEM_REQUESTER = new ItemRequesterBlock(C, ItemSettings.block().tooltip(TooltipSupplier.simple(1)), FabricBlockSettings.create().hardness(0.3f).sounds(BlockSoundGroup.METAL));
    public static final Block FILTERED_EJECTOR = new FilteredEjectorBlock(C, ItemSettings.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.create());
    public static final Block ITEM_PIPE = new ItemPipeBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(NMBlocks.ITEM_PIPE_SETTINGS));
    public static final Block OPAQUE_ITEM_PIPE = new ItemPipeBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(NMBlocks.ITEM_PIPE_SETTINGS));
    public static final Block ENCASED_ITEM_PIPE = new EncasedItemPipeBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(NMBlocks.ITEM_PIPE_SETTINGS));
    public static final Block MERGE_ITEM_PIPE = new MergePipeBlock(C, NMBlocks.block(), MeatlibBlockSettings.copyOf(NMBlocks.ITEM_PIPE_SETTINGS));
    public static final Block ITEM_PUMP = new ItemPumpBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));
    public static final Block EJECTOR = new EjectorBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));
    public static final Block ROUTER = new RouterBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS));
    public static final Block ADVANCED_ROUTER = new AdvancedRouterBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(ROUTER));
    public static final Block BUFFER = new BufferBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.CHEST).tags(AXE_MINEABLE));
    public static final Block DUMPER = new DumperBlock(C, NMBlocks.block().tooltip(TooltipSupplier.simple(1)), MeatlibBlockSettings.copyOf(Blocks.OAK_WOOD).tags(AXE_MINEABLE));

    public static BlockEntityType<FilteredEjectorBlockEntity> FILTERED_EJECTOR_BE;

    public static void init()
    {
        PIPE_DRIVER_BE = NMBlockEntities.register("pipe_driver", PipeDriverBlock.PipeDriverBlockEntity::new, PIPE_DRIVER);
        RoutingNetwork.LOOKUP.registerForBlockEntity(PipeDriverBlock.PipeDriverBlockEntity::getNetwork, PIPE_DRIVER_BE);
        STORAGE_BUS_BE = NMBlockEntities.register("storage_bus", StorageBusBlockEntity::new, STORAGE_BUS);
        RoutablePipe.LOOKUP.registerSelf(STORAGE_BUS_BE);
        ITEM_REQUESTER_BE = NMBlockEntities.register("item_requester", ItemRequesterBlockEntity::new, ITEM_REQUESTER);
        RoutablePipe.LOOKUP.registerSelf(ITEM_REQUESTER_BE);
        FILTERED_EJECTOR_BE = NMBlockEntities.register("filtered_ejector", (p, s) -> new FilteredEjectorBlockEntity(ItemTransport.FILTERED_EJECTOR_BE, p, s), FILTERED_EJECTOR);

        ITEM_PIPE_LOOKUP.registerFallback((world, pos, state, blockEntity, context) -> state.getBlock() instanceof ItemPipe pipe ? pipe : null);

        TransportScreenHandlers.ITEM_REQUESTER_HANDLER = ScreenHandlerInit.registerExtended(NeepMeat.NAMESPACE, "item_requester", ItemRequesterScreenHandler::new);
    }
}
