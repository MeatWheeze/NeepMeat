package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.block.multiblock.IMultiBlock;
import com.neep.neepmeat.block.multiblock.IPortBlock;
import com.neep.neepmeat.block.multiblock.PortBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemPortBlock extends BaseBlock implements IPortBlock<ItemVariant>, IVatComponent, BlockEntityProvider
{
    public ItemPortBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings);
    }

    public ItemPortBlock(String registryName, int itemMaxStack, boolean hasLore, ItemFactory factory, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, factory, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof ItemPortBlockEntity be && !world.isClient())
        {
            System.out.println(be.getController());
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new ItemPortBlockEntity(pos, state);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static class ItemPortBlockEntity extends PortBlockEntity<ItemVariant> implements IPortBlock.Entity
    {
        public ItemPortBlockEntity(BlockPos pos, BlockState state)
        {
            this(NMBlockEntities.VAT_ITEM_PORT, pos, state);
        }

        public ItemPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state, ItemVariant.class);
        }
    }
}
