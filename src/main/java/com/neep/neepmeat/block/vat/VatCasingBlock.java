package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.block.multiblock.IMultiBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VatCasingBlock extends BaseBlock implements IVatComponent, BlockEntityProvider
{
    public VatCasingBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (world.getBlockEntity(pos) instanceof Entity be && !world.isClient())
        {
            be.onParentBreak((ServerWorld) world);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.VAT_CASING.instantiate(pos, state);
    }
}
