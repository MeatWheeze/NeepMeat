package com.neep.neepmeat.machine.power_flower;

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.Set;

public class PowerFlowerControllerBlock extends BaseBlock implements BlockEntityProvider
{
    public PowerFlowerControllerBlock(String registryName, ItemSettings block, Settings settings)
    {
        super(registryName, block, settings.nonOpaque());
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context)
    {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.POWER_FLOWER_CONTROLLER.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.POWER_FLOWER_CONTROLLER, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }
}
