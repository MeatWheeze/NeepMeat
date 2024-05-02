package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PortBlock<T extends BlockEntity> extends BaseFacingBlock implements BlockEntityProvider
{
    private final Supplier<BlockEntityType<T>> factory;

    public PortBlock(String registryName, ItemSettings itemSettings, Supplier<BlockEntityType<T>> factory, Settings settings)
    {
        super(registryName, itemSettings, settings);
        this.factory = factory;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        if (context.getPlayer() == null)
            return getDefaultState();

        return this.getDefaultState().with(FACING, context.getPlayer().isSneaking() ? context.getPlayerLookDirection() : context.getPlayerLookDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return factory.get().instantiate(pos, state);
    }
}
