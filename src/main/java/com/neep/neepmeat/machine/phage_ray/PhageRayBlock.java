package com.neep.neepmeat.machine.phage_ray;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.AbstractBloodAcceptor;
import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import com.neep.neepmeat.util.MiscUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PhageRayBlock extends BigBlock<PhageRayBlock.PhageRayStructureBlock> implements MeatlibBlock, BlockEntityProvider
{
    private final String name;
    private final BigBlockPattern volume;
    private final VoxelShape shape = VoxelShapes.cuboid(-1, 0, -1, 2, 0.5, 2);

    public PhageRayBlock(String name, Settings settings)
    {
        super(settings);
        this.name = name;
        ItemRegistry.queue(NeepMeat.NAMESPACE, (MeatlibItem) new BaseBlockItem(this, name, ItemSettings.block().requiresVascular().tooltip(TooltipSupplier.hidden(1))));
        volume = BigBlockPattern.oddCylinder(1, 0, 0, getStructure().getDefaultState());
    }

    @Override
    protected PhageRayStructureBlock registerStructureBlock()
    {
        return BlockRegistry.queue(new PhageRayStructureBlock(this, FabricBlockSettings.copyOf(this)), "phage_ray_structure");
    }

    @Override
    protected BigBlockPattern getVolume(BlockState blockState)
    {
        return volume;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.PHAGE_RAY.instantiate(pos,state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.PHAGE_RAY, (world1, pos, state1, blockEntity) -> blockEntity.serverTick(), null, world);
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }

    public static class PhageRayStructureBlock extends BigBlockStructure<PhageRayStructureBlockEntity>
    {
        public PhageRayStructureBlock(BigBlock<?> parent, Settings settings)
        {
            super(parent, settings);
        }

        @Override
        public BlockRenderType getRenderType(BlockState state)
        {
            return BlockRenderType.MODEL;
        }

        @Override
        protected BlockEntityType<PhageRayStructureBlockEntity> registerBlockEntity()
        {
            return Registry.register(
                    Registry.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "phage_ray_structure"),
                    FabricBlockEntityTypeBuilder.create(
                            (p, s) -> new PhageRayStructureBlockEntity(getBlockEntityType(), p, s),
                            this).build());
        }
    }

    public static class PhageRayStructureBlockEntity extends BigBlockStructureEntity
    {
        @Nullable
        private BlockApiCache<Void, Void> cache;

        private final StructureBloodAcceptor acceptor = new StructureBloodAcceptor();

        public PhageRayStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public BloodAcceptor getAcceptor(Direction direction)
        {
            return acceptor;
        }

        private class StructureBloodAcceptor extends AbstractBloodAcceptor
        {
            @Override
            public Mode getMode()
            {
                return Mode.ACTIVE_SINK;
            }

            @Override
            public float updateInflux(float influx)
            {
                var be =  getControllerBE();
                if (be != null)
                    return be.getBloodAcceptor().updateInflux(influx);

                return 0;
            }
        }

        @Nullable
        private PhageRayBlockEntity getControllerBE()
        {
            if (controllerPos == null)
            {
                return null;
            }
            else if (cache == null)
            {
                cache = BlockApiCache.create(MeatLib.VOID_LOOKUP, (ServerWorld) getWorld(), controllerPos);
            }

            return cache.getBlockEntity() instanceof PhageRayBlockEntity controller ? controller : null;
        }
    }
}
