package com.neep.neepmeat.block.entity;

import com.neep.meatlib.block.IMeatBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CandleCronenCakeBlock extends CandleCakeBlock implements IMeatBlock
{
    private final String registryName;
    private final BlockItem blockItem;

    public CandleCronenCakeBlock(String registryName, Settings settings)
    {
        super(Blocks.WHITE_CANDLE, settings);
        this.registryName = registryName;
        this.blockItem = new BaseBlockItem(this, registryName, 64, 0, new FabricItemSettings().group(NMItemGroups.FOOD));
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
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        return NMBlocks.CRONENCAKE.getPickStack(world, pos, state);
    }

    private static boolean isHittingCandle(BlockHitResult hitResult)
    {
        return hitResult.getPos().y - (double)hitResult.getBlockPos().getY() > 0.5;
    }


}
