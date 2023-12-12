package com.neep.neepmeat.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.NMSoundGroups;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureBlockEntity;
import com.neep.neepmeat.api.big_block.BlockVolume;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class AdvancedIntegratorBlock extends BigBlock implements BlockEntityProvider
{
    public static final BlockVolume VOLUME = BlockVolume.oddCylinder(1, 0, 0);
    public static final VoxelShape SHAPE = VOLUME.toVoxelShape();

    public AdvancedIntegratorBlock(String registryName, Settings settings)
    {
        super(registryName, settings);
        ItemRegistry.queue(NeepMeat.NAMESPACE, (MeatlibItem) new BaseBlockItem(this, registryName, ItemSettings.block()));
    }

    @Override
    protected BigBlockStructure createStructure()
    {
//        return NMBlocks.ADVANCED_INTEGRATOR_STRUCTURE;
        return BlockRegistry.queue(new BigBlockStructure(this, "advanced_integrator_structure", FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL)));
    }

    @Override
    protected BlockEntityType<? extends BigBlockStructureBlockEntity> getBlockEntityType()
    {
        return NMBlockEntities.ADVANCED_INTEGRATOR_STRUCTURE;
    }

    @Override
    protected BlockVolume getVolume()
    {
        return VOLUME;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ADVANCED_INTEGRATOR.instantiate(pos, state);
    }
}
