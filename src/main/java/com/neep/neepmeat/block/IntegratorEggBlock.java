package com.neep.neepmeat.block;

import com.neep.neepmeat.block.base.BaseBlock;
import com.neep.neepmeat.blockentity.FluidBufferProvider;
import com.neep.neepmeat.blockentity.TankBlockEntity;
import com.neep.neepmeat.blockentity.integrator.IntegratorEggBlockEntity;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class IntegratorEggBlock extends BaseBlock implements BlockEntityProvider
{
    public IntegratorEggBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new IntegratorEggBlockEntity(pos, state);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> ticker, World world)
    {
        return expectedType == givenType && !world.isClient ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return checkType(type, BlockEntityInitialiser.INTEGRATOR_EGG, IntegratorEggBlockEntity::serverTick, world);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient)
        {
            if (world.getBlockEntity(pos) instanceof FluidBufferProvider be)
            {
                player.sendMessage(Text.of(Float.toString(be.getBuffer(null).getAmount() / (float) FluidConstants.BUCKET)), true);
            }
        }
        return ActionResult.SUCCESS;
    }
}
