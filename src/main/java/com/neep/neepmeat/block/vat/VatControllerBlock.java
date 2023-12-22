package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.api.multiblock.IMultiBlock;
import com.neep.neepmeat.block.entity.machine.VatControllerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VatControllerBlock extends BaseHorFacingBlock implements IMultiBlock, BlockEntityProvider, IVatComponent
{
    public static final BooleanProperty ASSEMBLED = BooleanProperty.of("assembled");

    public VatControllerBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return context.getPlayerLookDirection().getAxis().isVertical() ? getDefaultState() :
                this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite())
                        .with(ASSEMBLED, false);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        System.out.println(world.getBlockEntity(pos));
        if (world.getBlockEntity(pos) instanceof VatControllerBlockEntity be)
        {
            if (!be.isAssembled() && !world.isClient() && !player.isSneaking())
            {
                be.tryAssemble((ServerWorld) world);
            }
            else if (player.isSneaking())
            {
                System.out.println(be.blocks);
                System.out.println(be.getFluidStorage());
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && world.getBlockEntity(pos) instanceof VatControllerBlockEntity be && !world.isClient())
        {
            be.disassemble((ServerWorld) world, true);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(ASSEMBLED);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new VatControllerBlockEntity(pos, state);
    }
}
