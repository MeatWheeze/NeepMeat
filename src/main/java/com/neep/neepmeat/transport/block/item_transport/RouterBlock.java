package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.api.pipe.ItemPipe;
import com.neep.neepmeat.transport.block.item_transport.entity.RouterBlockEntity;
import com.neep.neepmeat.transport.item_network.ItemInPipe;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class RouterBlock extends BaseBlock implements BlockEntityProvider, ItemPipe
{
    public RouterBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient)
        {
            if (world.getBlockEntity(pos) instanceof RouterBlockEntity be)
            {
                player.openHandledScreen(be);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (world instanceof ServerWorld serverWorld)
            onChanged(pos, serverWorld);
        super.onStateReplaced(state, world, pos, newState, moved);
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
    public boolean singleOutput()
    {
        return false;
    }

    @Override
    public EnumSet<Direction> getConnections(BlockState state, Predicate<Direction> forbidden)
    {
        var set = EnumSet.allOf(Direction.class);
        set.removeIf(Predicate.not(forbidden));
        return set;
    }

    @Override
    public boolean prioritise()
    {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new RouterBlockEntity(pos, state);
    }
}
