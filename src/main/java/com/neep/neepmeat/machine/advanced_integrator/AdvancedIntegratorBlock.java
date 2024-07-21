package com.neep.neepmeat.machine.advanced_integrator;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItem;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
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

public class AdvancedIntegratorBlock extends BigBlock<AdvancedIntegratorStructure> implements MeatlibBlock, BlockEntityProvider, DataCable
{
    private final BigBlockPattern volume;

    public AdvancedIntegratorBlock(RegistrationContext ctx, Settings settings)
    {
        super(ctx, settings);
        ctx.append(this, new BaseBlockItem(this, ctx, ItemSettings.block()));
        volume = BigBlockPattern.makeOddCylinder(1, 0, 0, getStructure().getDefaultState());
    }

    @Override
    protected AdvancedIntegratorStructure registerStructureBlock(RegistrationContext ctx)
    {
        var block =  new AdvancedIntegratorStructure(this, settings);
        return ctx.append(this, block, "advanced_integrator_structure");
    }

    @Override
    public BigBlockPattern getVolume(BlockState blockState)
    {
        return volume;
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
        return MiscUtil.checkType(type, NMBlockEntities.ADVANCED_INTEGRATOR,
                (w, pos, state1, be) -> be.serverTick(),
                (w, pos, state1, be) -> be.clientTick(),
                world);
    }
}
