package com.neep.neepmeat.machine.large_crusher;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.api.multiblock2.MultiBlockStructure;
import com.neep.neepmeat.api.multiblock2.Multiblock2ControllerBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class LargeCrusherStructureBlock extends MultiBlockStructure<LargeCrusherStructureBlockEntity>
{
    private final String name;

    public LargeCrusherStructureBlock(String name, Multiblock2ControllerBlock<?> parent, Settings settings)
    {
        super(parent, settings);
        this.name = name;
    }

    @Override
    protected BlockEntityType<LargeCrusherStructureBlockEntity> registerBlockEntity()
    {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "large_crusher_structure"),
                FabricBlockEntityTypeBuilder.create(
                        (p, s) -> new LargeCrusherStructureBlockEntity(getBlockEntityType(), p, s),this)
                        .build());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        if (world.getBlockEntity(pos) instanceof BigBlockStructureEntity be)
        {
            BlockPos controllerPos = be.getControllerPos();
            if (controllerPos == null)
                return VoxelShapes.fullCube();

            BlockState parentState = world.getBlockState(controllerPos);
            if (parentState.isOf(parent)) // Sometimes air replaces the parent (not sure why)
                return be.translateChopShape(parent.getAssembledShape(parentState, world, pos, context));
        }
        return VoxelShapes.empty();
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }
}
