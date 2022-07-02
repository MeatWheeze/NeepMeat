package com.neep.neepmeat.block.item_transport;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.block.pipe.IItemPipe;
import com.neep.neepmeat.blockentity.pipe.RouterBlockEntity;
import com.neep.neepmeat.item_transfer.TubeUtils;
import com.neep.neepmeat.util.ItemInPipe;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RouterBlock extends BaseBlock implements BlockEntityProvider, IItemPipe
{
    public RouterBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Override
    public long insert(World world, BlockPos pos, BlockState state, Direction direction, ItemInPipe item)
    {
        Direction output = Direction.NORTH;
        if (direction != output)
        {
            TubeUtils.tryTransfer(item, pos, state, output, world);
            return item.getItemStack().getCount();
        }
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new RouterBlockEntity(pos, state);
    }
}
