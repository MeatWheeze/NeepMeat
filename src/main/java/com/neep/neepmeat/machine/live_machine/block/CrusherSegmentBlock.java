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
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrusherSegmentBlock extends BigBlock<CrusherSegmentBlock.CrusherSegmentStructureBlock> implements MeatlibBlock, BlockEntityProvider
{
    private final String registryName;
    private final BigBlockPattern pattern = new BigBlockPattern().oddCylinder(1, 0, 0, () -> getStructure().getDefaultState());

    public CrusherSegmentBlock(String registryName, Settings settings, ItemSettings itemSettings)
    {
        super(settings);
        itemSettings.create(this, registryName, itemSettings);
        this.registryName = registryName;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return LivingMachines.CRUSHER_SEGMENT_BE.instantiate(pos, state);
    }

    @Override
    protected CrusherSegmentStructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new CrusherSegmentStructureBlock(this, FabricBlockSettings.copyOf(settings)), "crusher_segment_structure");
    }

    @Override
    public BigBlockPattern getVolume(BlockState blockState)
    {
        return pattern;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, LivingMachines.CRUSHER_SEGMENT_BE, null, (world1, pos, state1, blockEntity) -> blockEntity.clientTick(), world);
    }

    public static class CrusherSegmentStructureBlock extends BigBlockStructure<CrusherSegmentStructureBlockEntity>
    {
        public CrusherSegmentStructureBlock(BigBlock<?> parent, Settings settings)
        {
            super(parent, settings);
        }

        @Override
        protected BlockEntityType<CrusherSegmentStructureBlockEntity> registerBlockEntity()
        {
            return NMBlockEntities.register("crusher_segment_structure", (p, s) -> new CrusherSegmentStructureBlockEntity(getBlockEntityType(), p, s), this);
        }
    }

    static class CrusherSegmentStructureBlockEntity extends BigBlockStructureEntity
    {
        public CrusherSegmentStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }
    }
}
