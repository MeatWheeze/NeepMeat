package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseColumnBlock;
import com.neep.neepmeat.blockentity.fluid.TankBlockEntity;
import com.neep.neepmeat.blockentity.machine.ItemPumpBlockEntity;
import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.font.TransformAttribute;

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
        if (world.getBlockEntity(pos) instanceof TankBlockEntity tank && tank.onUse(player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
