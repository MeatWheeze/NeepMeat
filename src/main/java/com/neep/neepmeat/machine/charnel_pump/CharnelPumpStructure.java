package com.neep.neepmeat.machine.charnel_pump;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class CharnelPumpStructure extends BigBlockStructure<CharnelPumpStructure.CPSBlockEntity>
{
    public CharnelPumpStructure(BigBlock<?> parent, Settings settings)
    {
        super(parent, settings);
    }

    @Override
    protected BlockEntityType<CPSBlockEntity> registerBlockEntity()
    {
        return Registry.register(
                Registry.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "charnel_pump_structure"),
                FabricBlockEntityTypeBuilder.create(
                        (p, s) -> new CharnelPumpStructure.CPSBlockEntity(getBlockEntityType(), p, s),
                        this).build());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.fullCube();
    }

    public static class CPSBlockEntity extends BigBlockStructureEntity
    {
        public CPSBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public BloodAcceptor getAcceptor(Direction direction)
        {
            if (apis.contains(BloodAcceptor.SIDED.getId()))
            {
                return new BloodAcceptor()
                {
                    @Override
                    public Mode getMode()
                    {
                        return Mode.SOURCE;
                    }

                    @Override
                    public long getOutput()
                    {
                        return 100;
                    }
                };
            }
            return null;
        }
    }
}
