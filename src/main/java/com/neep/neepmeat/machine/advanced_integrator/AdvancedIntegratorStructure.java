package com.neep.neepmeat.machine.advanced_integrator;

import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class AdvancedIntegratorStructure extends BigBlockStructure
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
    public @Nullable BigBlockStructureBlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ADVANCED_INTEGRATOR_STRUCTURE.instantiate(pos, state);
    }
}
