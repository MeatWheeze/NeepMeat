package com.neep.neepmeat.machine.mixer;

import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.util.ItemUtil;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MixerBlock extends TallBlock implements BlockEntityProvider
{
    public MixerBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings.nonOpaque());
    }

    @Override
    protected Structure createStructure()
    {
        return BlockRegistry.queue(new Structure(getRegistryName() + "_structure", MeatlibBlockSettings.copyOf(settings))
        {
            @Override
            public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
            {
                if (ItemUtil.playerHoldingPipe(player, hand))
                    return super.onUse(state, world, pos, player, hand, hit);

                if (world.getBlockEntity(pos.down()) instanceof MixerBlockEntity be)
                    return ActionResult.success(ItemUtil.singleVariantInteract(player, hand, be.getItemStorage(null)));

                return super.onUse(state, world, pos, player, hand, hit);
            }
        });
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtil.playerHoldingPipe(player, hand))
            return super.onUse(state, world, pos, player, hand, hit);

        if (world.getBlockEntity(pos) instanceof MixerBlockEntity be)
            return ActionResult.success(ItemUtil.singleVariantInteract(player, hand, be.getItemStorage(null)));

        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(newState.getBlock()))
        {
            if (world.getBlockEntity(pos) instanceof MixerBlockEntity be)
            {
                be.dropItems();
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MixerBlockEntity(pos, state);
    }

}