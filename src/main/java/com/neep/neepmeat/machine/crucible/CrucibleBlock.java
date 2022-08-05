package com.neep.neepmeat.machine.crucible;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import com.neep.neepmeat.blockentity.ItemBufferBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrucibleBlock extends BaseBlock implements BlockEntityProvider
{
    public CrucibleBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof CrucibleBlockEntity be && !world.isClient)
        {
            WritableFluidBuffer.handleInteract(be.getStorage().fluidStorage, world, player, hand);
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.CRUCIBLE.instantiate(pos, state);
    }
}
