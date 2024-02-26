package com.neep.neepmeat.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.ItemRegistry;
import com.neep.neepmeat.block.entity.MetalBarrelBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MetalBarrelBlock extends BarrelBlock implements MeatlibBlock
{
    private final String name;

    public MetalBarrelBlock(String name, Settings settings)
    {
        super(settings);
        this.name = name;
        ItemRegistry.queue(new BaseBlockItem(this, name, ItemSettings.block()));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.METAL_BARREL.instantiate(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        if (itemStack.hasCustomName() && world.getBlockEntity(pos) instanceof MetalBarrelBlockEntity be)
        {
            be.setCustomName(itemStack.getName());
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.isClient)
        {
            return ActionResult.SUCCESS;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof MetalBarrelBlockEntity be)
        {
            player.openHandledScreen(be);
        }

        return ActionResult.CONSUME;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getSide());
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }
}
