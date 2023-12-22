package com.neep.neepmeat.machine.centrifuge;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructureBlockEntity;
import com.neep.neepmeat.api.big_block.BlockVolume;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Pair;
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
    protected Pair<Structure, BlockEntityType<? extends BigBlockStructureBlockEntity>> createStructure()
    {
        Structure structure = (Structure) BlockRegistry.queue(new Structure(getRegistryName() + "_structure", FabricBlockSettings.copyOf(this.settings)));
        BlockEntityType<BigBlockStructureBlockEntity> type = NMBlockEntities.registerBlockEntity(getRegistryName(), structure::createBlockEntity, this);
        return new Pair<>(structure, type);
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
}
