package com.neep.neepmeat.machine.upgrade_manager;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.implant.player.ImplantManager;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.screen_handler.UpgradeManagerScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class UpgradeManagerBlockEntity extends SyncableBlockEntity implements ExtendedScreenHandlerFactory
{
    public UpgradeManagerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Nullable
    public ImplantManager getImplantManager()
    {
        MutateInPlace<Entity> itemMip = MutateInPlace.ENTITY.find(world, pos.offset(getCachedState().get(UpgradeManagerBlock.FACING)), null);
        if (itemMip != null)
        {
            Entity entity = itemMip.get();
            if (entity != null) // Shouldn't ever be null, but just in case.
            {
                return NMComponents.IMPLANT_MANAGER.getNullable(entity);
            }
        }
        return null;
    }

    @Override
    public Text getDisplayName()
    {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new UpgradeManagerScreenHandler(syncId, inv, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        PacketBufUtil.writeBlockPos(buf, pos);
    }
}
