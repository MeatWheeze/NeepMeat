package com.neep.neepmeat.transport.machine.item;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
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

public class FilteredEjectorBlock extends EjectorBlock
{
    public FilteredEjectorBlock(String registryName, ItemSettings itemSettings, FabricBlockSettings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof FilteredEjectorBlockEntity be)
        {
            if (player.isSneaking() && player.getMainHandStack().isEmpty())
            {
                be.changeMode();
                return ActionResult.SUCCESS;
            }
            else
            {
                player.openHandledScreen(be);
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return ItemTransport.FILTERED_EJECTOR_BE.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, ItemTransport.FILTERED_EJECTOR_BE, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }
}
