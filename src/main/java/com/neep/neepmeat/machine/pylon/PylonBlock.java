package com.neep.neepmeat.machine.pylon;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PylonBlock extends TallBlock implements BlockEntityProvider, DataCable
{
    public static final VoxelShape OUTLINE = Block.createCuboidShape(0, 0, 0, 16, 32 + 16, 16);

    public PylonBlock(RegistrationContext ctx, Settings settings)
    {
        super(ctx, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return OUTLINE;
    }

    @Override
    protected Structure createStructure(RegistrationContext ctx)
    {
        return ctx.append(this, new PylonStructure(ctx, MeatlibBlockSettings.copyOf(this.settings)), MeatlibBlock::structure);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.PYLON.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.PYLON, PylonBlockEntity::serverTick, (world1, pos, state1, blockEntity) -> blockEntity.clientTick(), world);
    }

    public class PylonStructure extends Structure
    {
        public PylonStructure(RegistrationContext ctx, Settings settings)
        {
            super(settings);
        }
    }
}
