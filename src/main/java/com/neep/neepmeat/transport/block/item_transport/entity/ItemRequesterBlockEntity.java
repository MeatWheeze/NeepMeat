package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.ItemRequester;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.item_network.RoutingNetworkCache;
import com.neep.neepmeat.transport.item_network.RoutingNetworkDFSFinder;
import com.neep.neepmeat.transport.network.SyncRequesterScreenS2CPacket;
import com.neep.neepmeat.transport.screen_handler.ItemRequesterScreenHandler;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ItemRequesterBlockEntity extends ItemPipeBlockEntity implements ItemRequester, ExtendedScreenHandlerFactory
{
    public static final int W_GRID = 9;
    public static final int H_GRID = 7;

    protected ImplementedInventory inventory = ImplementedInventory.ofSize(W_GRID * H_GRID);

    protected RoutingNetworkCache networkCache;

    protected PropertyDelegate propertyDelegate = new ArrayPropertyDelegate(ItemRequesterScreenHandler.PROPERTIES);

    public ItemRequesterBlockEntity(BlockPos pos, BlockState state)
    {
        this(ItemTransport.ITEM_REQUESTER_BE, pos, state);
    }

    public ItemRequesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public long requestItem(ItemVariant variant, long amount, NodePos fromPos, TransactionContext transaction)
    {
        return 0;
    }

    @Override
    public Stream<StorageView<ItemVariant>> getAvailable(TransactionContext transaction)
    {
        return Stream.empty();
    }

    protected BlockApiCache<RoutingNetwork, Void> getController()
    {
        RoutingNetworkDFSFinder finder = new RoutingNetworkDFSFinder(world);
        finder.pushBlock(pos, Direction.UP);
        finder.loop(ItemTransport.BFS_MAX_DEPTH);
        return finder.hasResult() ? finder.getResult().right() : null;
    }

    @Override
    public Text getDisplayName()
    {
        return Text.translatable("screen." + NeepMeat.NAMESPACE + "item_requester");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        BlockApiCache<RoutingNetwork, Void> cache1 = getController();

        if (cache1 == null) return null;

        RoutingNetwork network = cache1.find(null);

        if (network == null) return null;

        return new ItemRequesterScreenHandler(syncId, inv, inventory, network, pos, player, propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        BlockApiCache<RoutingNetwork, Void> cache1 = getController();

        if (cache1 == null) return;

        RoutingNetwork network = cache1.find(null);

        if (network == null) return;

        ItemRequesterScreenHandler.syncToPacket(buf, new ArrayList<>(), network);
    }
}
