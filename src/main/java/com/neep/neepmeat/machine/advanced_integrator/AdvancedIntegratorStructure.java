package com.neep.neepmeat.machine.advanced_integrator;

import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureBlockEntity;
import com.neep.neepmeat.block.entity.AdvancedIntegratorBlockEntity;
import com.neep.neepmeat.block.entity.AdvancedIntegratorStructureBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AdvancedIntegratorStructure extends BigBlockStructure implements DataCable
{
    public AdvancedIntegratorStructure(BigBlock parent, String registryName, Settings settings)
    {
        super(parent, registryName, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return parent.getOutlineShape(state, world, pos, context);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!player.getStackInHand(hand).isEmpty())
            return super.onUse(state, world, pos, player, hand, hit);

        if (!world.isClient() && world.getBlockEntity(pos) instanceof AdvancedIntegratorStructureBlockEntity be)
        {
            var parent = be.getParent();
            if (parent != null)
            {
                parent.onUse(player);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BigBlockStructureBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ADVANCED_INTEGRATOR_STRUCTURE.instantiate(pos, state);
    }
}
