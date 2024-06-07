package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class LargeFanBlock extends BigBlock<LargeFanBlock.LargeFanStructureBlock> implements MeatlibBlock, BlockEntityProvider
{
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;

    private final String name;

    private final Map<Direction.Axis, BigBlockPattern> patternMap;

    public LargeFanBlock(String name, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        BigBlockPattern upPattern = BigBlockPattern.makeOddCylinder(1, 0, 0, getStructure().getDefaultState());
        BigBlockPattern northPattern = BigBlockPattern.makeRange(-1, -1, 0, 1, 1, 0, getStructure().getDefaultState());
        ItemRegistry.queue(new BaseBlockItem(this, name, itemSettings, new MeatlibItemSettings().group(NMItemGroups.GENERAL)));
        this.name = name;

        patternMap = new EnumMap<>(Map.of(
                Direction.Axis.X, northPattern.rotateY(90),
                Direction.Axis.Z, northPattern,
                Direction.Axis.Y, upPattern
        ));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return getDefaultState().with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    protected LargeFanStructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new LargeFanStructureBlock(this, MeatlibBlockSettings.copyOf(this)), "large_fan_structure");
    }

    @Override
    public BigBlockPattern getVolume(BlockState blockState)
    {
        return patternMap.get(blockState.get(AXIS));
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(AXIS);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.LARGE_FAN.instantiate(pos, state);
    }

    public static class LargeFanStructureBlock extends BigBlockStructure<LargeFanStructureEntity>
    {
        public LargeFanStructureBlock(BigBlock<?> parent, Settings settings)
        {
            super(parent, settings);
        }

        @Override
        protected BlockEntityType<LargeFanStructureEntity> registerBlockEntity()
        {
            return Registry.register(
                    Registry.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "large_fan_structure"),
                    FabricBlockEntityTypeBuilder.create(
                            (p, s) -> new LargeFanStructureEntity(getBlockEntityType(), p, s),
                            this).build());
        }
    }

    public static class LargeFanStructureEntity extends BigBlockStructureEntity
    {
        public LargeFanStructureEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }
    }
}
