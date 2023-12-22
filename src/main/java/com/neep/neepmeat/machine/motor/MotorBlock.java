package com.neep.neepmeat.machine.motor;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.block.BaseVertFacingBlock;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.ItemUtils;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
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

        if (world.getBlockEntity(pos) instanceof MotorBlockEntity be && !world.isClient())
        {
            player.sendMessage(Text.of(Float.toString(be.getRunningRate())), true);
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos().offset(context.getSide().getOpposite());
        if (world.getBlockEntity(pos) instanceof IMotorisedBlock)
        {
            BlockState state = world.getBlockState(pos);
            if(state.getBlock() instanceof BaseFacingBlock)
            {
                return getDefaultState().with(FACING, state.get(BaseFacingBlock.FACING));
            }
            else if (state.getBlock() instanceof BaseVertFacingBlock)
            {
                return getDefaultState().with(FACING, state.get(BaseVertFacingBlock.FACING));
            }
        }
        return super.getPlacementState(context);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof MotorBlockEntity be && !world.isClient())
        {
            be.update((ServerWorld) world, pos, fromPos, state);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.MOTOR, (world1, pos, state1, blockEntity) -> blockEntity.tick(), null, world);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MotorBlockEntity(pos, state);
    }
}
