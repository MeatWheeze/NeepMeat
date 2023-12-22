package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.transport.interfaces.IServerWorld;
import com.neep.neepmeat.transport.util.TubeUtils;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Stack;

public class RouteTestBlock extends BaseBlock
{
    public RouteTestBlock(String registryName, Settings settings)
    {
        super(registryName, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            BlockPos startPos = pos;
            BlockPos endPos = pos.north(10).east(2);
            ItemInPipe item = new ItemInPipe(null, null, ItemVariant.of(Items.STONE), 1, world.getTime());
            Stack<Direction> route = ((IServerWorld) serverWorld).getItemNetwork().findPath(pos, Direction.NORTH, endPos, Direction.SOUTH, ItemVariant.of(Items.STONE), 1);
            item.setRoute(route);
            try (Transaction transaction = Transaction.openOuter())
            {
                TubeUtils.pipeToAny(item, pos, Direction.NORTH, world, transaction, false);
            }
            
        }
        return ActionResult.SUCCESS;
    }
}
