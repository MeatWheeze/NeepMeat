package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.block.entity.FlameJetBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class FlameJetBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public static final BooleanProperty RUNNING = BooleanProperty.of("running");

    public FlameJetBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, FluidComponentItem::new, settings);
        setDefaultState(getStateManager().getDefaultState().with(RUNNING, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof FlameJetBlockEntity be)
        {
            return ActionResult.success(be.onUse(player, hand));
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof FlameJetBlockEntity be)
        {
            if (be.isPowered() != world.isReceivingRedstonePower(pos))
            {
                be.setPowered(!be.isPowered());
            }
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.FLAME_JET.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.FLAME_JET, FlameJetBlockEntity::serverTick, FlameJetBlockEntity::clientTick, world);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(RUNNING);
    }
}
