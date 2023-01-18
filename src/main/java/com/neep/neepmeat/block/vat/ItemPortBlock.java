package com.neep.neepmeat.block.vat;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.api.multiblock.IPortBlock;
import com.neep.neepmeat.api.multiblock.PortBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ItemPortBlock extends VatCasingBlock implements IPortBlock<ItemVariant>, IVatComponent, BlockEntityProvider
{
    public ItemPortBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof BlockEntity be && !world.isClient())
        {
            System.out.println(be.getController());
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public net.minecraft.block.entity.BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new BlockEntity(pos, state);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static class BlockEntity extends PortBlockEntity<ItemVariant> implements IPortBlock.Entity
    {
        public BlockEntity(BlockPos pos, BlockState state)
        {
            this(NMBlockEntities.VAT_ITEM_PORT, pos, state);
        }

        public BlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state, ItemVariant.class);
        }
    }
}
