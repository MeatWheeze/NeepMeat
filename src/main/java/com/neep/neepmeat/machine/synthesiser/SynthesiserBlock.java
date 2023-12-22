package com.neep.neepmeat.machine.synthesiser;

import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SynthesiserBlock extends TallBlock implements BlockEntityProvider
{
    public SynthesiserBlock(String registryName, Settings settings)
    {
        super(registryName, settings.nonOpaque());
    }

    @Override
    protected Structure getStructure()
    {
        return (Structure) BlockRegistry.queue(new Structure(getRegistryName() + "_structure", FabricBlockSettings.copyOf(this.settings)));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.SYNTHESISER.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.SYNTHESISER, (world1, pos, state1, blockEntity) -> blockEntity.tick(), world);
    }
}
