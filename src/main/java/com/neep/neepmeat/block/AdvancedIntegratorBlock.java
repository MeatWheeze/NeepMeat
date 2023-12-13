package com.neep.neepmeat.block;

import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.NMSoundGroups;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureBlockEntity;
import com.neep.neepmeat.api.big_block.BlockVolume;
import com.neep.neepmeat.block.entity.AdvancedIntegratorBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.advanced_integrator.AdvancedIntegratorStructure;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AdvancedIntegratorBlock extends BigBlock implements BlockEntityProvider, DataCable
{
    public static final BlockVolume VOLUME = BlockVolume.oddCylinder(1, 0, 0);
//    public static final VoxelShape SHAPE = VOLUME.toVoxelShape();

    public AdvancedIntegratorBlock(String registryName, Settings settings)
    {
        super(registryName, settings.nonOpaque());
        ItemRegistry.queue(NeepMeat.NAMESPACE, (MeatlibItem) new BaseBlockItem(this, registryName, ItemSettings.block()));
    }

    @Override
    protected BigBlockStructure createStructure()
    {
        return BlockRegistry.queue(new AdvancedIntegratorStructure(this, "advanced_integrator_structure", FabricBlockSettings.of(Material.METAL).strength(3.0f).sounds(NMSoundGroups.METAL)));
    }

    @Override
    protected BlockEntityType<? extends BigBlockStructureBlockEntity> getBlockEntityType()
    {
        return NMBlockEntities.ADVANCED_INTEGRATOR_STRUCTURE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.getStackInHand(hand).isEmpty())
        {
            AdvancedIntegratorBlockEntity be = world.getBlockEntity(pos, NMBlockEntities.ADVANCED_INTEGRATOR).orElse(null);
            if (be != null && !world.isClient())
            {
                be.onUse(player);
            }
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    protected BlockVolume getVolume()
    {
        return VOLUME;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return super.getOutlineShape(state, world, pos, context);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.ADVANCED_INTEGRATOR.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.ADVANCED_INTEGRATOR, (w, pos, state1, be) -> be.serverTick(), null, world);
    }
}
