package com.neep.neepmeat.machine.fluid_exciter;

import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.transport.api.pipe.VascularConduit;
import com.neep.neepmeat.transport.api.pipe.VascularConduitEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FluidExciterBlock extends TallBlock implements BlockEntityProvider
{
    public FluidExciterBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings.factory(FluidComponentItem::new), settings.nonOpaque());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        world.getBlockEntity(pos, NMBlockEntities.FLUID_EXCITER).ifPresent(FluidExciterBlockEntity::updateCache);
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Override
    protected Structure createStructure()
    {
        return BlockRegistry.queue(new FluidExciterStructure(getRegistryName() + "_structure", FabricBlockSettings.copyOf(this.settings)));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.FLUID_EXCITER.instantiate(pos, state);
    }

    @Nullable
    public static VascularConduitEntity getConduitFromTop(World world, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, Void unused)
    {
        if (world.getBlockEntity(pos.down()) instanceof FluidExciterBlockEntity be)
            return be.getConduitEntity(unused);

        return null;
    }

    private class FluidExciterStructure extends Structure implements VascularConduit
    {
        public FluidExciterStructure(String registryName, Settings settings)
        {
            super(registryName, settings);
        }

        @Override
        public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
        {
            super.onPlaced(world, pos, state, placer, itemStack);
        }

        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
        {
            super.onBlockAdded(state, world, pos, oldState, notify);
            updatePosition(world, pos, state, VascularConduitEntity.UpdateReason.ADDED);
        }

        @Override
        public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
        {
            if (VascularConduit.find(world, sourcePos, world.getBlockState(sourcePos)) == null && !sourceBlock.equals(this))
            {
                updatePosition(world, pos, state, VascularConduitEntity.UpdateReason.CHANGED);
            }
        }

//        @Override
//        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
//        {
//            if (player.getStackInHand(hand).isEmpty())
//            {
//                if (!world.isClient() && world.getBlockEntity(pos.down()) instanceof FluidExciterBlockEntity be)
//                {
//                    System.out.println(be.getConduitEntity(null).getNetwork());
//                }
//                return ActionResult.SUCCESS;
//            }
//            return super.onUse(state, world, pos, player, hand, hit);
//        }

        @Override
        public void updatePosition(World world, BlockPos pos, BlockState state, VascularConduitEntity.UpdateReason reason)
        {
            VascularConduit.super.updatePosition(world, pos, state, reason);
        }

        @Override
        public boolean isConnectedIn(BlockView world, BlockPos pos, BlockState state, Direction direction)
        {
            return true;
        }

        @Override
        public VascularConduitEntity getEntity(World world, BlockPos pos, BlockState state)
        {
            if (world.getBlockEntity(pos.down()) instanceof FluidExciterBlockEntity be)
            {
                return be.getConduitEntity(null);
            }
            throw new IllegalStateException();
        }
    }
}
