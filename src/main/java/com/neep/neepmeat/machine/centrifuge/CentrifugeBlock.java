package com.neep.neepmeat.machine.centrifuge;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CentrifugeBlock extends BigBlock
{
    public static final BigBlockPattern VOLUME = BigBlockPattern.makeOddCylinder(1, 0, 0, null);
    public static final VoxelShape SHAPE = VOLUME.toVoxelShape();

    public CentrifugeBlock(String registryName, Settings settings)
    {
        super(settings);
        ItemRegistry.queue(NeepMeat.NAMESPACE, (MeatlibItem) new BaseBlockItem(this, registryName, ItemSettings.block()));
    }

    @Override
    protected BigBlockStructure registerStructureBlock()
    {
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    @Override
    protected BigBlockPattern getVolume(BlockState blockState)
    {
        return VOLUME;
    }
}
