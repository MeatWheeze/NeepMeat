package com.neep.neepmeat.machine.upgrade_manager;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.screen_handler.UpgradeManagerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class UpgradeManagerBlockEntity extends SyncableBlockEntity implements NamedScreenHandlerFactory
{
    public UpgradeManagerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
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
        return new UpgradeManagerScreenHandler(syncId, inv);
    }
}
