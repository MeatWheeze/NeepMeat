package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FilteredEjectorBlock extends EjectorBlock
{
    public FilteredEjectorBlock(String registryName, ItemSettings itemSettings, FabricBlockSettings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return ItemTransport.FILTERED_EJECTOR_BE.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, ItemTransport.FILTERED_EJECTOR_BE, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }
}
