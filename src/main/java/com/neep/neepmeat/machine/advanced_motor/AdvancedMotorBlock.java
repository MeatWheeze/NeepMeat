package com.neep.neepmeat.machine.advanced_motor;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class AdvancedMotorBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public AdvancedMotorBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.getStackInHand(hand).isOf(NMBlocks.VASCULAR_CONDUIT.asItem()))
        {
            return ActionResult.PASS;
        }

        if (world.getBlockEntity(pos) instanceof AdvancedMotorBlockEntity be && !world.isClient())
        {
            player.sendMessage(PowerUtils.perUnitToText(be.getMechPUPower()), true);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return adjacentMotorisedDirection(context, this);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!state.isOf(newState.getBlock()))
        {
            world.getBlockEntity(pos, NMBlockEntities.ADVANCED_MOTOR).ifPresent(MotorEntity::onRemoved);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    public static BlockState adjacentMotorisedDirection(ItemPlacementContext context, Block block)
    {
        World world = context.getWorld();
        Direction facing = context.getSide();
        BlockPos pos = context.getBlockPos().offset(context.getSide().getOpposite());
        if (world.getBlockEntity(pos) instanceof MotorisedBlock)
        {
            return block.getDefaultState().with(FACING, facing.getOpposite());
        }
        else
        {
            BlockPos.Mutable mutable = context.getBlockPos().mutableCopy();
            for (Direction direction : Direction.values())
            {
                mutable.set(context.getBlockPos(), direction);
                if (world.getBlockEntity(mutable) instanceof MotorisedBlock)
                {
                    return block.getDefaultState().with(FACING, direction);
                }
            }
        }

        return block.getDefaultState().with(FACING, context.getPlayer().isSneaking() ?
                context.getPlayerLookDirection().getOpposite() : context.getPlayerLookDirection());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.ADVANCED_MOTOR, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ADVANCED_MOTOR.instantiate(pos, state);
    }
}
