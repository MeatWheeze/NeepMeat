package com.neep.neepmeat.plc.block;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.plc.PLCBlocks;
import com.neep.neepmeat.plc.block.entity.ExecutorBlockEntity;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ExecutorBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public ExecutorBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return PLCBlocks.EXECUTOR_ENTITY.instantiate(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.isSneaking() && world.getBlockEntity(pos) instanceof ExecutorBlockEntity be)
        {
            if (!world.isClient())
                be.stop();

            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, PLCBlocks.EXECUTOR_ENTITY, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }
}
