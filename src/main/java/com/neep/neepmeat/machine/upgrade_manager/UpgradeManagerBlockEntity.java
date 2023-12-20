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
import net.minecraft.util.Identifier;
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
        MutateInPlace<?> mip = getMip();
        if (mip != null)
        {
            Object object = mip.get();
            if (object != null)
            {
                return NMComponents.IMPLANT_MANAGER.getNullable(object);
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

    public MutateInPlace<?> getMip()
    {
        MutateInPlace<?> mip = MutateInPlace.ITEM.find(world, pos.offset(getCachedState().get(UpgradeManagerBlock.FACING)), null);
        if (mip == null)
        {
            mip = MutateInPlace.ENTITY.find(world, pos.offset(getCachedState().get(UpgradeManagerBlock.FACING)), null);
        }
        return mip;
    }

    public void removeUpgrade(Identifier identifier)
    {
        MutateInPlace<Object> mip = (MutateInPlace<Object>) getMip();
        if (mip != null)
        {
            Object object = mip.get();
            if (object != null)
            {
                ImplantManager manager = NMComponents.IMPLANT_MANAGER.getNullable(object);
                if (manager != null)
                {
                    manager.removeImplant(identifier);
                    mip.set(object);
                }
            }
        }
    }
}
