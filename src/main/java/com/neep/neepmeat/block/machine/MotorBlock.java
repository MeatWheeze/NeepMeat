package com.neep.neepmeat.block.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.blockentity.machine.MotorBlockEntity;
import com.neep.neepmeat.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MotorBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public MotorBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtils.playerHoldingPipe(player, hand))
            return ActionResult.PASS;

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().offset(context.getSide().getOpposite());
        if (world.getBlockEntity(pos) instanceof IMotorisedBlock)
        {
            return getDefaultState().with(FACING, world.getBlockState(pos).get(BaseFacingBlock.FACING));
        }
        else
        {
            return super.getPlacementState(context);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof MotorBlockEntity be && !world.isClient())
        {
            be.update(world, pos, fromPos, state);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MotorBlockEntity(pos, state);
    }
}
