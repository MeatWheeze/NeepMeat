package com.neep.neepmeat.machine.integrator;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IntegratorBlock extends BaseBlock implements BlockEntityProvider, DataCable
{
    public IntegratorBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new IntegratorBlockEntity(pos, state);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> ticker, World world)
    {
        return expectedType == givenType && !world.isClient ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return checkType(type, NMBlockEntities.INTEGRATOR, IntegratorBlockEntity::serverTick, world);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (player.getStackInHand(hand).isOf(NMItems.SACRIFICIAL_SCALPEL))
            return ActionResult.PASS;

        if (!world.isClient)
        {
            if (world.getBlockEntity(pos) instanceof IntegratorBlockEntity be)
            {
                if (!be.takeFromHand(player, hand))
                    be.showContents((ServerPlayerEntity) player);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
    {
        super.onBreak(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder)
    {
        return super.getDroppedStacks(state, builder);
    }

    //    @Override
//    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
//    {
//        super.randomDisplayTick(state, world, pos, random);
//        if (world.getBlockEntity(pos) instanceof IntegratorBlockEntity be)
//        {
//            Box box = new Box(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3);
//            List<Entity> players = be.getWorld().getEntitiesByType(TypeFilter.instanceOf(Entity.class), box, (e) -> true);
//            if (players.size() > 0)
//            {
//                be.setLookPos(players.get(0).getBlockPos());
//            }
//        }
//    }
}
