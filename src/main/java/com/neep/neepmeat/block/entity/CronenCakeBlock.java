package com.neep.neepmeat.block.entity;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.item.MeatlibItemSettings;
import com.neep.meatlib.item.TooltipSupplier;
import com.neep.neepmeat.NMItemGroups;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class CronenCakeBlock extends CakeBlock implements MeatlibBlock
{
    private final String registryName;
    private final BlockItem blockItem;

    public CronenCakeBlock(String registryName, Settings settings)
    {
        super(settings);
        this.registryName = registryName;
        this.blockItem = new BaseBlockItem(this, registryName, ItemSettings.block().tooltip(TooltipSupplier.simple(1)), new MeatlibItemSettings().group(NMItemGroups.FOOD));
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
        Item item = itemStack.getItem();
        if (itemStack.isIn(ItemTags.CANDLES) && state.get(BITES) == 0 && Block.getBlockFromItem(item) instanceof CandleBlock)
        {
            if (!player.isCreative())
            {
                itemStack.decrement(1);
            }
            world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.playSound(null, pos, NMSounds.CAKE_SCREAM, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.setBlockState(pos, NMBlocks.CANDLE_CRONENCAKE.getDefaultState());
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.incrementStat(Stats.USED.getOrCreateStat(item));
            return ActionResult.SUCCESS;
        }
        if (world.isClient)
        {
            if (CakeBlock.tryEat(world, pos, state, player).isAccepted())
            {
                return ActionResult.SUCCESS;
            }
            if (itemStack.isEmpty())
            {
                return ActionResult.CONSUME;
            }
        }
        return CakeBlock.tryEat(world, pos, state, player);
    }

    protected static ActionResult tryEatCake(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        return tryEat(world, pos, state, player);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
    }

    @Override
    public boolean autoGenDrop()
    {
        return false;
    }
}
