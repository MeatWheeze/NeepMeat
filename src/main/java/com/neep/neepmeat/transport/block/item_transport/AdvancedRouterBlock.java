package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.item_transport.entity.RouterBlockEntity;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AdvancedRouterBlock extends RouterBlock
{
    public AdvancedRouterBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public boolean canItemEnter(ResourceAmount<ItemVariant> item, World world, BlockPos pos, BlockState state, Direction inFace)
    {
        if (world.getBlockEntity(pos) instanceof RouterBlockEntity be)
        {
            Direction output = be.getOutputDirection(item.resource());
            return inFace != output && output != null;
        }
        return false;
    }

    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item, TransactionContext transaction)
    {
        if (world.getBlockEntity(pos) instanceof RouterBlockEntity be)
        {
            Direction output = be.getOutputDirection(item.resource());
            if (direction != output && output != null)
            {
                long transferred = ItemPipeUtil.pipeToAny(item, pos, output, world, transaction, false);
                return transferred;
            }
        }
        return 0;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ADVANCED_ROUTER.instantiate(pos, state);
    }
}
