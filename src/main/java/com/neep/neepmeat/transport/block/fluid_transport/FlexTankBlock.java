package com.neep.neepmeat.transport.block.fluid_transport;

import com.google.common.collect.Sets;
import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FlexTankBlockEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public class FlexTankBlock extends BaseBlock implements BlockEntityProvider
{
    public final long capacity;
    private final Supplier<BlockEntityType<FlexTankBlockEntity>> typeSupplier;

    public FlexTankBlock(String registryName, long capacity, Supplier<BlockEntityType<FlexTankBlockEntity>> type, ItemSettings itemSettings, Settings settings)
    {
        super(registryName,itemSettings, settings);
        this.capacity = capacity;
        this.typeSupplier = type;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (world.getBlockEntity(pos) instanceof FlexTankBlockEntity be)
        {
            FlexTankBlockEntity.updateConnections(world, be, typeSupplier.get());
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!newState.isOf(this) && !world.isClient())
        {
            // Get the block entity before it is removed
            if (world.getBlockEntity(pos) instanceof FlexTankBlockEntity be)
            {
                // Block entity will be removed here
                super.onStateReplaced(state, world, pos, newState, moved);

                Set<FlexTankBlockEntity> adjacent = Sets.newHashSet();

                BlockPos.Mutable mutable = pos.mutableCopy();
                for (Direction direction : Direction.values())
                {
                    mutable.set(pos, direction);

//                    if (world.getBlockEntity(mutable) instanceof FlexTankBlockEntity adj)
                    world.getBlockEntity(pos, typeSupplier.get()).ifPresent(adjacent::add);
                }

                for (FlexTankBlockEntity adj : adjacent)
                {
                    var root = FlexTankBlockEntity.updateConnections(world, adj, typeSupplier.get());
                    be.setRoot(root); // Move as much fluid as possible into the new root(s).
                }
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && world.getBlockEntity(pos) instanceof FlexTankBlockEntity be)
        {
            FlexTankBlockEntity.updateConnections(world, be, typeSupplier.get());
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.getStackInHand(hand).isEmpty())
        {
            if (!world.isClient() && world.getBlockEntity(pos) instanceof FlexTankBlockEntity be)
            {
                player.sendMessage(Text.of(be.getRoot().getPos().toString() + " " + be.getSize()));

            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
//        return NMBlockEntities.FLEX_TANK.instantiate(pos, state);
        return new FlexTankBlockEntity(typeSupplier.get(), pos, state, capacity);
    }
}
