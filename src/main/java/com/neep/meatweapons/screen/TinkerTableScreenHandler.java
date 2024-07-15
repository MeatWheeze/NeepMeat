package com.neep.meatweapons.screen;

import com.neep.meatlib.api.network.ChannelFormat;
import com.neep.meatlib.api.network.ParamCodec;
import com.neep.meatlib.network.ChannelManager;
import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.init.MWScreenHandlers;
import com.neep.meatweapons.item.meatgun.Meatgun;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.item.meatgun.MeatgunModuleItem;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.meatgun.module.ModuleSlot;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class TinkerTableScreenHandler extends BasicScreenHandler
{
    public static final int BACKGROUND_WIDTH = 340;
    public static final int BACKGROUND_HEIGHT = 200;

    public static final Identifier CHANNEL_ID = new Identifier(MeatWeapons.NAMESPACE, "chunnel");
    public static final ChannelFormat<SlotClick> CHANNEL_FORMAT = ChannelFormat.builder(SlotClick.class)
            .param(ParamCodec.UUID)
            .param(ParamCodec.INT)
            .build();

    public final ChannelManager<SlotClick> slotClick;

    public TinkerTableScreenHandler(int syncId, PlayerInventory playerInventory)
    {
        this(syncId, playerInventory, new SimpleInventory(1));
    }

    public TinkerTableScreenHandler(int syncId, PlayerInventory playerInventory, Inventory blockInv)
    {
        super(MWScreenHandlers.MEATGUN, playerInventory, blockInv, syncId, null);
        this.slotClick = ChannelManager.create(CHANNEL_ID, CHANNEL_FORMAT, playerInventory.player);

        addSlot(new Slot(blockInv, 0, 8, BACKGROUND_HEIGHT - 2 - 21)
        {
            @Override
            public boolean canInsert(ItemStack stack)
            {
                return stack.getItem() instanceof Meatgun;
            }
        });
        createInventory(5 + 24, BACKGROUND_HEIGHT - 80, playerInventory);
        createHotbar(5 + 24, BACKGROUND_HEIGHT - 23, playerInventory);

        slotClick.receiver(this::onSlotClick);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot)
    {
        return super.quickMove(player, slot);
//        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        super.onClosed(player);
        slotClick.close();
    }

    public void onSlotClick(UUID uuid, int slotIdx)
    {
        MeatgunComponent meatgun = MWComponents.MEATGUN.getNullable(getSlot(0).getStack());
        if (meatgun != null)
        {
            MeatgunModule parent = meatgun.find(uuid);
            if (parent != null)
            {
                ModuleSlot slot1 = parent.getChildren().get(slotIdx);

                boolean slotEmpty = slot1.get() == MeatgunModule.DEFAULT;
                boolean canChange = true;
                if (!slotEmpty)
                {
                    for (var childSlot : slot1.get().getChildren())
                    {
                        if (childSlot.get() != MeatgunModule.DEFAULT)
                        {
                            canChange = false;
                        }
                    }
                }

                boolean cursorEmpty = getCursorStack().isEmpty();

                if (!slotEmpty && canChange)
                {
                    if (cursorEmpty)
                    {
                        ItemStack moduleStack = MeatgunModuleItem.get(slot1.get().getType());

                        if (moduleStack.isEmpty())
                            return;

                        setCursorStack(moduleStack);
                        slot1.set(MeatgunModule.DEFAULT);
                        syncState();
                    }
                    else if (getCursorStack().getCount() == 1)
                    {
                        // Swap
                        MeatgunModule.Type<?> cursorType = MeatgunModuleItem.get(getCursorStack());
                        if (cursorType != MeatgunModule.DEFAULT_TYPE)
                        {
                            setCursorStack(MeatgunModuleItem.get(slot1.get().getType()));

                            slot1.set(cursorType.create(meatgun.getListener(), parent));
                            syncState();
                        }
                    }
                }
                else if (slotEmpty && canChange)
                {
                    MeatgunModule.Type<?> cursorType = MeatgunModuleItem.get(getCursorStack());
                    if (cursorType != MeatgunModule.DEFAULT_TYPE)
                    {
                        slot1.set(cursorType.create(meatgun.getListener(), parent));
                        getCursorStack().decrement(1);
                        syncState();
                    }
                }
            }
        }
    }

    public interface SlotClick
    {
        void apply(UUID uuid, int slot);
    }
}
