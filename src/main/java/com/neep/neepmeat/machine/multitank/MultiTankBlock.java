package com.neep.neepmeat.machine.multitank;

import com.neep.neepmeat.block.fluid_transport.GlassTankBlock;
import com.neep.neepmeat.blockentity.fluid.TankBlockEntity;
import com.neep.neepmeat.util.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MultiTankBlock extends GlassTankBlock
{
    public MultiTankBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MultiTankBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtils.playerHoldingPipe(player, hand))
            return ActionResult.PASS;

        if (world.getBlockEntity(pos) instanceof MultiTankBlockEntity tank && tank.onUse(player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}