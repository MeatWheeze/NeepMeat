package com.neep.neepmeat.machine.centrifuge;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureBlockEntity;
import com.neep.neepmeat.api.big_block.BlockVolume;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CentrifugeBlock extends BigBlock
{
    public static final BlockVolume VOLUME = BlockVolume.oddCylinder(1, 0, 0);
    public static final VoxelShape SHAPE = VOLUME.toVoxelShape();

    public CentrifugeBlock(String registryName, Settings settings)
    {
        super(registryName, settings);
        ItemRegistry.queue(NeepMeat.NAMESPACE, (MeatlibItem) new BaseBlockItem(this, registryName, ItemSettings.block()));
    }

    @Override
    protected BigBlockStructure createStructure()
    {
//        Structure structure = BlockRegistry.queue(new Structure(getRegistryName() + "_structure", FabricBlockSettings.copyOf(this.settings)));
//        BlockEntityType<BigBlockStructureBlockEntity> type = NMBlockEntities.register(getRegistryName(), structure::createBlockEntity, structure);
//        return new Pair<>(structure, type);
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    @Override
    protected BlockVolume getVolume()
    {
        return VOLUME;
    }

    @Override
    protected BlockEntityType<? extends BigBlockStructureBlockEntity> getBlockEntityType()
    {
        return null;
    }
}
