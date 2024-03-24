package com.neep.neepmeat.api.multiblock2;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.init.NMBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
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

public class TestMultiblock extends Multiblock2ControllerBlock<TestMultiblock.TMStructureBlock> implements MeatlibBlock
{
    private final BigBlockPattern assembledPattern;
    private final MultiblockUnassembledPattern unassembledPattern;
    private final String name;

    private final VoxelShape shape = VoxelShapes.cuboid(-1, 0, -1, 2, 3, 2);

    public TestMultiblock(String name, Settings settings)
    {
        super(settings);
        this.name = name;

        assembledPattern = new BigBlockPattern().oddCylinder(
                1, 1, 2, () -> getStructure().getDefaultState()
        );

        unassembledPattern = new MultiblockUnassembledPattern().oddCylinder(
                1, 1, 2, () -> NMBlocks.MEAT_STEEL_BLOCK.getDefaultState()
        );

        ItemRegistry.queue(new BaseBlockItem(this, name, ItemSettings.block(), new MeatlibItemSettings()));
    }

    @Override
    protected TMStructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new TMStructureBlock(this, FabricBlockSettings.copyOf(settings)), "test_multiblock_structure");
    }

    @Override
    protected BigBlockPattern getAssembledPattern(BlockState blockState)
    {
        return assembledPattern;
    }

    @Override
    protected MultiblockUnassembledPattern getUnassembledPattern(BlockState blockState)
    {
        return unassembledPattern;
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.fullCube();
    }

    public static class TMStructureBlock extends MultiBlockStructure<TMStructureBlockEntity>
    {
        public TMStructureBlock(Multiblock2ControllerBlock<?> parent, Settings settings)
        {
            super(parent, settings);
        }

        @Override
        protected BlockEntityType<TMStructureBlockEntity> registerBlockEntity()
        {
            return Registry.register(
                    Registries.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "test_multiblock_structure"),
                    FabricBlockEntityTypeBuilder.create(
                                    (p, s) -> new TMStructureBlockEntity(getBlockEntityType(), p, s),this)
                            .build());
        }

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
        {
            return VoxelShapes.fullCube();
        }
    }

    public static class TMStructureBlockEntity extends BigBlockStructureEntity
    {

        public TMStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }
    }
}
