package com.neep.neepmeat.block.entity;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class CandleCronenCakeBlock extends CandleCakeBlock implements MeatlibBlock
{
    private final String registryName;

    public CandleCronenCakeBlock(String registryName, Settings settings)
    {
        super(Blocks.WHITE_CANDLE, settings);
        this.registryName = registryName;
    }

    @Override
    public String getRegistryName()
    {
        return registryName;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.FLINT_AND_STEEL) || itemStack.isOf(Items.FIRE_CHARGE))
        {
            return ActionResult.PASS;
        }
        if (!(isHittingCandle(hit) && player.getStackInHand(hand).isEmpty() && state.get(LIT)))
        {
            ActionResult actionResult = CronenCakeBlock.tryEatCake(world, pos, NMBlocks.CRONENCAKE.getDefaultState(), player);
            if (actionResult.isAccepted())
            {
                CandleCakeBlock.dropStacks(state, world, pos);
            }
            return actionResult;
        }
        CandleCakeBlock.extinguish(player, state, world, pos);
        return ActionResult.success(world.isClient);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (newState.isOf(this) && newState.get(LIT) && !state.get(LIT))
        {
            world.playSound(null, pos, NMSounds.CAKE_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        return NMBlocks.CRONENCAKE.getPickStack(world, pos, state);
    }

    private static boolean isHittingCandle(BlockHitResult hitResult)
    {
        return hitResult.getPos().y - (double)hitResult.getBlockPos().getY() > 0.5;
    }


}
