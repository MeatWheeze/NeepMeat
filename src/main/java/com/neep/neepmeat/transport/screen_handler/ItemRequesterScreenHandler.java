package com.neep.neepmeat.transport.screen_handler;

import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity.H_GRID;
import static com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity.W_GRID;

public class ItemRequesterScreenHandler extends BasicScreenHandler
{
    protected RoutingNetwork routingNetwork;
    protected BlockPos pos;

    // Client
    public ItemRequesterScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new ItemRequesterBlockEntity.Inventory(W_GRID * H_GRID), RoutingNetwork.DEFAULT, new BlockPos(0, 0, 0));
    }

    // Server
    public ItemRequesterScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, RoutingNetwork network, BlockPos pos)
    {
        super(ItemTransport.ITEM_REQUESTER_HANDLER, playerInventory, inventory, syncId, null);
        this.routingNetwork = network;
        this.pos = pos;
        checkSize(inventory, 1);
        createPlayerSlots(8, 174, playerInventory);
        createSlots();
    }

    protected void createSlots()
    {
        int w_slot = 18;
        int h_slot = 18;
        for (int j = 0; j < H_GRID; ++j)
        {
            for (int i = 0; i < W_GRID; ++i)
            {
                this.addSlot(new Slot(inventory, i + j * W_GRID, 8 + i * w_slot, 8 + h_slot + j * w_slot));
            }
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player)
    {
        if (isInGrid(slotIndex))
        {
            ItemStack stack = getStacks().get(slotIndex);

            if (stack.isEmpty()) return;

            ResourceAmount<ItemVariant> resourceAmount = new ResourceAmount<>(ItemVariant.of(stack), stack.getCount());
            try (Transaction transaction = Transaction.openOuter())
            {
                routingNetwork.request(resourceAmount, pos, Direction.UP, RoutingNetwork.RequestType.ANY_AMOUNT, transaction);
                ((ItemRequesterBlockEntity.Inventory) inventory).updateInventory(routingNetwork, transaction);
                transaction.commit();
            }
            return;
        }
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    protected boolean isInGrid(int index)
    {
        return index >= 36 && index < 36 + W_GRID * H_GRID;
    }
}