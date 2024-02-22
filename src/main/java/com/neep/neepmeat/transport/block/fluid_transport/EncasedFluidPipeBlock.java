package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.EncasedBlock;
import com.neep.neepmeat.transport.block.EncasedBlockEntity;
import com.neep.neepmeat.util.ItemUtil;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EncasedFluidPipeBlock extends FluidPipeBlock implements EncasedBlock
{
    public EncasedFluidPipeBlock(String itemName, PipeCol col, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, col, itemSettings, settings);
    }

    public VoxelShape getPipeOutlineShape(BlockState state, BlockView world, BlockPos pos)
    {
        return super.getOutlineShape(state, world, pos, ShapeContext.absent());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
        if (view.getBlockEntity(pos) instanceof EncasedBlockEntity be)
        {
            return be.getCamoShape();
        }
        return super.getOutlineShape(state, view, pos, context);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (replaceUse(state, world, pos, player, hand, hit))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ENCASED_FLUID_PIPE.instantiate(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.ENCASED_FLUID_PIPE, (world1, pos, state1, blockEntity) -> blockEntity.tick(), null, world);
    }

    @Override
    public boolean canReplace(ItemStack stack, BlockItem blockItem)
    {
        return !(blockItem.getBlock() instanceof EncasedBlock) && !ItemUtil.checkFluidComponent(blockItem);
    }
}
