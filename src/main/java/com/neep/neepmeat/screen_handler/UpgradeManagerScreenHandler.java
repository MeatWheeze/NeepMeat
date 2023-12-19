package com.neep.neepmeat.screen_handler;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.machine.upgrade_manager.UpgradeManagerBlockEntity;
import com.neep.neepmeat.implant.player.ImplantManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class UpgradeManagerScreenHandler extends BasicScreenHandler
{
    private final UpgradeManagerBlockEntity manager;

    public UpgradeManagerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf)
    {
        super(ScreenHandlerInit.UPGRADE_MANAGER, playerInventory, new SimpleInventory(1), syncId, null);
        BlockPos pos = PacketBufUtil.readBlockPos(buf);
        this.manager = (UpgradeManagerBlockEntity) playerInventory.player.world.getBlockEntity(pos);
    }

    // Server
    public UpgradeManagerScreenHandler(int syncId, PlayerInventory pi, UpgradeManagerBlockEntity manager)
    {
        super(ScreenHandlerInit.UPGRADE_MANAGER, pi, null, syncId, null);
        this.manager = manager;
    }

    @Nullable
    public ImplantManager getImplantManager()
    {
        return manager.getImplantManager();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player)
    {
        return true;
    }
}