package com.neep.neepmeat.transport.screen_handler;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.network.SyncRequesterScreenS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

import static com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity.H_GRID;
import static com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity.W_GRID;

@SuppressWarnings("UnstableApiUsage")
public class ItemRequesterScreenHandler extends BasicScreenHandler
{
    public static final int PROPERTIES = 1;

    protected RoutingNetwork routingNetwork;
    protected BlockPos pos;
    protected final PlayerEntity player;

    protected final List<ResourceAmount<ItemVariant>> items = new ArrayList<>(20);

    // Client
    public ItemRequesterScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        this(syncId, playerInventory, ImplementedInventory.ofSize(W_GRID * H_GRID), RoutingNetwork.DEFAULT, new BlockPos(0, 0, 0), playerInventory.player, new ArrayPropertyDelegate(PROPERTIES));
        receivePacket(buf);
    }

    // Server
    public ItemRequesterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, RoutingNetwork network, BlockPos pos, PlayerEntity player, PropertyDelegate delegate)
    {
        super(TransportScreenHandlers.ITEM_REQUESTER_HANDLER, playerInventory, inventory, syncId, delegate);
        this.routingNetwork = network;
        this.pos = pos;
        this.player = player;
        checkSize(inventory, 1);
        createPlayerSlots(8, 149, playerInventory);
    }

    protected void createSlots()
    {
        int w_slot = 18;
        int h_slot = 18;
        for (int j = 0; j < H_GRID; ++j)
        {
            for (int i = 0; i < W_GRID; ++i)
            {
                this.addSlot(new Slot(inventory, i + j * W_GRID, 8 + i * w_slot, 8 + j * h_slot));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player)
    {
//        if (isInGrid(slotIndex))
//        {
//            int index = slotIndex - 36;
//            if (items.size() <= index) return;
//
//            ResourceAmount<ItemVariant> ra = items.get(slotIndex - 36);
//
//            if (ra.resource().isBlank()) return;
//
//            long amount = Math.min(64, ra.amount());
//            ResourceAmount<ItemVariant> resourceAmount = new ResourceAmount<>(ra.resource(), Screen.hasShiftDown() ? 1 : amount);
//
//            try (Transaction transaction = Transaction.openOuter())
//            {
//                routingNetwork.request(resourceAmount, pos, Direction.UP, RoutingNetwork.RequestType.ANY_AMOUNT, transaction);
//                transaction.commit();
//            }
//            syncState();
//            return;
//        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public void syncState()
    {
        if (player instanceof ServerPlayerEntity serverPlayer)
        {
            PacketByteBuf buf = syncToPacket(PacketByteBufs.create(), items, routingNetwork);
            ServerPlayNetworking.send(serverPlayer, SyncRequesterScreenS2CPacket.SYNC_ID, buf);
        }
        super.syncState();
    }

    public static PacketByteBuf syncToPacket(PacketByteBuf buf, List<ResourceAmount<ItemVariant>> items, RoutingNetwork network)
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            items.clear();
            items.addAll(network.getAllAvailable(transaction));
            transaction.commit();
        }
        sortItems(items, 0);
        return SyncRequesterScreenS2CPacket.encodeSync(buf, items);
    }

    protected static void sortItems(List<ResourceAmount<ItemVariant>> items, int type)
    {
        items.sort((s1, s2) -> s1.resource().toStack().isEmpty() ? 1 : s1.resource().toStack().getName().asTruncatedString(256).compareTo(s2.resource().toStack().getName().asTruncatedString(256)));
    }

    public static boolean isInGrid(int index)
    {
        return index >= 36 && index < 36 + W_GRID * H_GRID;
    }

    public void receivePacket(PacketByteBuf buf)
    {
        SyncRequesterScreenS2CPacket.decodeSync(buf, items);
    }

    public List<ResourceAmount<ItemVariant>> getItems()
    {
        return items;
    }

    public void receiveRequestPacket(PacketByteBuf buf)
    {
        ResourceAmount<ItemVariant> ra = SyncRequesterScreenS2CPacket.decodeRequest(buf);
        try (Transaction transaction = Transaction.openOuter())
        {
            routingNetwork.request(ra, pos, Direction.UP, RoutingNetwork.RequestType.ANY_AMOUNT, transaction);
            transaction.commit();
        }
        syncState();
    }
}