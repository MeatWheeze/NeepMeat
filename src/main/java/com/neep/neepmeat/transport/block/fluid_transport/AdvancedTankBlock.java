package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.machine.fluid.TankBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class AdvancedTankBlock extends TankBlock
{
    public AdvancedTankBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ADVANCED_TANK.instantiate(pos, state);
    }

    public static <T extends TankBlockEntity> FabricBlockEntityTypeBuilder.Factory<T> makeBlockEntity(BlockEntityType<T> type)
    {
        return (pos, state) -> (T) new TankBlockEntity(type, pos, state, 16 * FluidConstants.BUCKET);
    }
}
