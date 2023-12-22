package com.neep.neepmeat.block;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.api.block.pipe.IDataCable;
import com.neep.neepmeat.blockentity.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.NMMaths;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class IntegratorBlock extends BaseBlock implements BlockEntityProvider, IDataCable
{
    public IntegratorBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
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
