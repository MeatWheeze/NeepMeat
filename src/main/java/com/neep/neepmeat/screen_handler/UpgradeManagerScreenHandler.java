package com.neep.neepmeat.screen_handler;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.implant.player.ImplantManager;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.machine.upgrade_manager.UpgradeManagerBlockEntity;
import com.neep.neepmeat.plc.component.MutateInPlace;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class UpgradeManagerScreenHandler extends BasicScreenHandler
{
    private final UpgradeManagerBlockEntity manager;
    public static final Identifier HANDLER_ID = new Identifier(NeepMeat.NAMESPACE, "upgrade_manager");

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

        ServerPlayNetworking.registerReceiver(((ServerPlayerEntity) pi.player).networkHandler,
                HANDLER_ID, ((server, player, handler, buf, responseSender) ->
                {
                    PacketByteBuf copy = PacketByteBufs.copy(buf);
                    server.execute(() ->
                    {
                        if (player.currentScreenHandler instanceof UpgradeManagerScreenHandler h)
                        {
                            h.apply(copy);
                        }
                    });
                }));
    }

    private void apply(PacketByteBuf buf)
    {
        manager.removeUpgrade(buf.readIdentifier());
    }

    @Nullable
    public ImplantManager getImplantManager()
    {
        return manager.getImplantManager();
    }

    @Nullable
    public MutateInPlace<?> getMip()
    {
        return manager.getMip();
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

    @Override
    public void close(PlayerEntity player)
    {
        if (!player.world.isClient())
        {
            ServerPlayNetworking.unregisterReceiver(((ServerPlayerEntity) player).networkHandler, HANDLER_ID);
        }
    }
}