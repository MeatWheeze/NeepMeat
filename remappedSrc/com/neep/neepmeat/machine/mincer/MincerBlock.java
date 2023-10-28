package com.neep.neepmeat.machine.mincer;

import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MincerBlock extends TallBlock implements BlockEntityProvider
{
    public static final BooleanProperty RUNNING = BooleanProperty.of("running");
    public static final VoxelShape OUTLINE = Block.createCuboidShape(0, 0, 0, 16, 29, 16);

    public MincerBlock(String registryName, Settings settings)
    {
        super(registryName, settings);
        this.setDefaultState(getStateManager().getDefaultState().with(RUNNING, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return OUTLINE;
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
        return NMBlockEntities.MINCER.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        BlockEntityTicker<MincerBlockEnity> clientTicker = state.get(RUNNING) ? (world1, pos, state1, be) -> be.clientTickRunning(world1) : null;
        return MiscUtils.checkType(type, NMBlockEntities.MINCER, (world1, pos, state1, be) -> be.serverTick(world1), clientTicker, world);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(RUNNING);
    }
}
