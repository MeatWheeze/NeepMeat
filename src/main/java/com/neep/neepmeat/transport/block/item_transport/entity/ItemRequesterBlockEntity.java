package com.neep.neepmeat.transport.block.item_transport.entity;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.ItemRequester;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.item_network.RoutingNetworkDFSFinder;
import com.neep.neepmeat.transport.screen_handler.ItemRequesterScreenHandler;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class ItemRequesterBlockEntity extends ItemPipeBlockEntity implements ItemRequester, NamedScreenHandlerFactory
{
    public static final int W_GRID = 9;
    public static final int H_GRID = 7;

    protected Inventory inventory = new SimpleInventory(W_GRID * H_GRID);

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
        finder.loop(50);
        return finder.getResult().right();
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText("screen." + NeepMeat.NAMESPACE + "item_requester");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        RoutingNetwork network = getController().find(null);
        try (Transaction transaction = Transaction.openOuter())
        {
            inventory.clear();
            List<ResourceAmount<ItemVariant>> items = network.getAllAvailable(transaction);
            for (int i = 0; i < W_GRID * H_GRID && i < items.size(); ++i)
            {
                ResourceAmount<ItemVariant> amount = items.get(i);
                inventory.setStack(i, amount.resource().toStack((int) amount.amount()));
            }
            transaction.commit();
            return new ItemRequesterScreenHandler(syncId, inv, inventory);
        }
    }
}
