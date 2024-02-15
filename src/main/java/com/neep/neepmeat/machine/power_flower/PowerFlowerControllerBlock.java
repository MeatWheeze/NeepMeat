package com.neep.neepmeat.machine.power_flower;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PowerFlowerControllerBlock extends BaseBlock implements BlockEntityProvider, PowerFlower
{
    public static final BooleanProperty VALID = BooleanProperty.of("valid");

    public PowerFlowerControllerBlock(String registryName, ItemSettings block, Settings settings)
    {
        super(registryName, block, settings.nonOpaque());
        setDefaultState(getDefaultState().with(VALID, true));
    }

    @Override
    public TagKey<Block> getPreferredTool()
    {
        return BlockTags.AXE_MINEABLE;
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

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(VALID);
    }
}
