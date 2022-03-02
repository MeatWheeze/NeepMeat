package com.neep.neepmeat.block;

import com.neep.neepmeat.block.base.BaseColumnBlock;
import com.neep.neepmeat.blockentity.fluid.GlassTankBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlassTankBlock extends BaseColumnBlock implements BlockEntityProvider
{
    public GlassTankBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
//        return new BlockEntityInitialiser.GLASS_TANK_BLOCK_ENTITY.createpos, state);
        return new GlassTankBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient)
        {
            if (world.getBlockEntity(pos) instanceof GlassTankBlockEntity be)
            {
                player.sendMessage(Text.of(Float.toString(be.getBuffer(null).getAmount() / (float) FluidConstants.BUCKET)), true);
            }
        }
        return ActionResult.SUCCESS;
    }
}
