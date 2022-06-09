package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.blockentity.machine.VatControllerBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
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
import org.lwjgl.system.CallbackI;

import java.util.Iterator;

public class ItemPortBlock extends BaseBlock implements IPortBlock<ItemVariant>, IVatStructure, BlockEntityProvider
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

    public static class ItemPortBlockEntity extends BlockEntity implements Storage<ItemVariant>, IVatStructure.Entity
    {
        protected VatControllerBlockEntity controller;

        public ItemPortBlockEntity(BlockPos pos, BlockState state)
        {
            this(NMBlockEntities.VAT_ITEM_PORT, pos, state);
        }

        public ItemPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
        {
            return 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
        {
            return 0;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator(TransactionContext transaction)
        {
            return null;
        }

        @Override
        public VatControllerBlockEntity getController()
        {
            return controller;
        }

        @Override
        public void setController(VatControllerBlockEntity controller)
        {
            this.controller = controller;
        }
    }
}
