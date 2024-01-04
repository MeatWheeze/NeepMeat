package com.neep.neepmeat.api.multiblock2;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public abstract class Multiblock2BlockEntity extends SyncableBlockEntity
{
    @Nullable private BlockPos controllerPos;
    @Nullable private BlockApiCache<Void, Void> controllerCache;

    public Multiblock2BlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }


}
