package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.blockentity.machine.VatControllerBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VatControllerBlock extends BaseHorFacingBlock implements IVatStructure, BlockEntityProvider
{
    public VatControllerBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return context.getPlayerLookDirection().getAxis().isVertical() ? getDefaultState() :
                this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite());
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof VatControllerBlockEntity be)
        {
            if (!be.isAssembled() && !world.isClient())
            {
                be.tryAssemble((ServerWorld) world);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new VatControllerBlockEntity(pos, state);
    }
}
