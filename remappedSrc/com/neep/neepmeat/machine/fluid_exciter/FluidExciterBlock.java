package com.neep.neepmeat.machine.fluid_exciter;

import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.item.FluidComponentItem;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluidExciterBlock extends TallBlock implements BlockEntityProvider
{
    public FluidExciterBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings.factory(FluidComponentItem::new), settings.nonOpaque());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        world.getBlockEntity(pos, NMBlockEntities.FLUID_EXCITER).ifPresent(FluidExciterBlockEntity::updateCache);
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Override
    protected Structure getStructure()
    {
        return BlockRegistry.queue(new Structure(getRegistryName() + "_structure", FabricBlockSettings.copyOf(this.settings)));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.FLUID_EXCITER.instantiate(pos, state);
    }
}
