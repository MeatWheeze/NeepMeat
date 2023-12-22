package com.neep.neepmeat.block.fluid_transport;

import com.neep.meatlib.block.BaseColumnBlock;
import com.neep.neepmeat.block.pipe.IFluidPipe;
import com.neep.neepmeat.blockentity.fluid.TankBlockEntity;
import com.neep.neepmeat.util.ItemUtils;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TankBlock extends BaseColumnBlock implements BlockEntityProvider
{
    public TankBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new TankBlockEntity(pos, state);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
//        ItemStack stack = super.getPickStack(world, pos, state);
//        NbtCompound nbt = new NbtCompound();
//        if (world.getBlockEntity(pos) instanceof TankBlockEntity be)
//        {
//            if (!be.getBuffer(null).isResourceBlank())
//            {
//                be.writeNbt(nbt);
//                stack.writeNbt(nbt);
//            }
//        }
        return super.getPickStack(world, pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtils.playerHoldingPipe(player, hand))
            return ActionResult.PASS;

        if (world.getBlockEntity(pos) instanceof TankBlockEntity tank && tank.onUse(player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
