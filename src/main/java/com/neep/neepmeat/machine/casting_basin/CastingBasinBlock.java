package com.neep.neepmeat.machine.casting_basin;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.ItemUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CastingBasinBlock extends BaseBlock implements BlockEntityProvider
{
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 14, 16);

    public CastingBasinBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof CastingBasinBlockEntity be)
        {
            ItemUtil.singleVariantInteract(player, hand, be.getStorage().outputStorage);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!world.getBlockState(pos).isOf(this))
        {
            world.getBlockEntity(pos, NMBlockEntities.CASTING_BASIN).ifPresent(be ->
            {
                ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        be.getStorage().outputStorage.getAsStack());
            });
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state)
    {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos)
    {
        if (world.getBlockEntity(pos) instanceof CastingBasinBlockEntity be)
        {
            return StorageUtil.calculateComparatorOutput(be.storage.outputStorage);
        }
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.CASTING_BASIN.instantiate(pos, state);
    }

}
