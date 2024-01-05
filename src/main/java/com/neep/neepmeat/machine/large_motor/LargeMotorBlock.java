package com.neep.neepmeat.machine.large_motor;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BlockVolume;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class LargeMotorBlock extends BigBlock<LargeMotorStructureBlock> implements MeatlibBlock
{
    private final String registryName;
    public static final BlockVolume VOLUME = BlockVolume.range(
            -1, 0, 0, 1, 2, -1
    );


    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public LargeMotorBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        ItemRegistry.queue(new BaseBlockItem(this, registryName, itemSettings));
        this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected LargeMotorStructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new LargeMotorStructureBlock(this, FabricBlockSettings.of(Material.METAL)), "large_motor_structure");
    }

    @Override
    protected BlockVolume getVolume(BlockState blockState)
    {
        Direction facing = blockState.get(FACING);
        return VOLUME.rotateY(facing.asRotation() - 180);
    }

//    @Override
//    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
//    {
//        VoxelShape s;
//        VoxelShapes.
//        s.forEachBox((n) ->
//        {
//        });
//        return Vox
//    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }
}
