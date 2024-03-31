package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class LargestHopperBlock extends BigBlock<LargestHopperBlock.StructureBlock> implements MeatlibBlock, BlockEntityProvider
{
    private final String registryName;
    private final BigBlockPattern pattern = new BigBlockPattern().oddCylinder(1, 0, 0, () -> getStructure().getDefaultState());

    public LargestHopperBlock(String registryName, Settings settings, ItemSettings itemSettings)
    {
        super(settings);
        itemSettings.create(this, registryName, itemSettings);
        this.registryName = registryName;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return LivingMachines.LARGEST_HOPPER_BE.instantiate(pos, state);
    }

    @Override
    protected StructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new StructureBlock(this, FabricBlockSettings.copyOf(settings)), "largest_hopper_structure");
    }

    @Override
    protected BigBlockPattern getVolume(BlockState blockState)
    {
        return pattern;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    public static class StructureBlock extends BigBlockStructure<StructureBlockEntity>
    {
        public StructureBlock(BigBlock<?> parent, Settings settings)
        {
            super(parent, settings);
        }

        @Override
        protected BlockEntityType<StructureBlockEntity> registerBlockEntity()
        {
            return NMBlockEntities.register("largest_hopper_structure", (p, s) -> new StructureBlockEntity(getBlockEntityType(), p, s), this);
        }
    }

    static class StructureBlockEntity extends BigBlockStructureEntity
    {
        public StructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }
    }
}
