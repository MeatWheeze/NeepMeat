package com.neep.neepmeat.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.api.pipe.AbstractPipeBlock;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DataCableBlock extends AbstractPipeBlock implements DataCable
{
    public DataCableBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Override
    public boolean canConnectTo(BlockState toState, Direction toFace, World world, BlockPos toPos)
    {
        if (toState.getBlock() instanceof DataCable cable)
        {
            return cable.connectInDirection(world, toPos, toState, toFace);
        }
        return false;
    }

    @Override
    public boolean connectInDirection(BlockView world, BlockPos pos, BlockState state, Direction direction)
    {
        return state.get(DIR_TO_CONNECTION.get(direction)).canBeChanged();
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos)
    {
        PipeConnectionType type = state.get(DIR_TO_CONNECTION.get(direction));
        boolean forced = type == PipeConnectionType.FORCED;
        boolean otherConnected = false;

        boolean canConnect = canConnectTo(neighborState, direction.getOpposite(), (World) world, neighborPos);
//        if (!world.isClient() && !(neighborState.getBlock() instanceof IItemPipe))
//        {
//            canConnect = canConnect || (canConnectApi((World) world, pos, state, direction));
//        }

        // Check if neighbour is forced
        if (neighborState.getBlock() instanceof DataCableBlock)
        {
            forced = forced || neighborState.get(DIR_TO_CONNECTION.get(direction.getOpposite())) == PipeConnectionType.FORCED;
            otherConnected = neighborState.get(DIR_TO_CONNECTION.get(direction.getOpposite())) == PipeConnectionType.SIDE;

        }

        // AAAAAAAAAAAAAAAAAAAA
        PipeConnectionType finalConnection =
                otherConnected ? PipeConnectionType.SIDE :
                        forced ? PipeConnectionType.FORCED
                                : canConnect ? PipeConnectionType.SIDE : PipeConnectionType.NONE;

        return state.with(DIR_TO_CONNECTION.get(direction), finalConnection);
    }
}
