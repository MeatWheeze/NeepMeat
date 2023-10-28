package com.neep.neepmeat.machine.trough;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TroughBlock extends BaseHorFacingBlock implements BlockEntityProvider
{
    public TroughBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque().ticksRandomly());
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        return this.getDefaultState().with(FACING, context.getPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return Block.createCuboidShape(0, 0, 0, 16, 11, 16);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof TroughBlockEntity be && WritableFluidBuffer.handleInteract(be.getStorage(null), world, player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.FEEDING_TROUGH.instantiate(pos, state);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type)
    {
        return false;
    }

    @Override
    public boolean hasRandomTicks(BlockState state)
    {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (!world.isClient())
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                Box box = Box.from(new BlockBox(pos).expand(5));
                List<AnimalEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(AnimalEntity.class), box,
                        e -> e.getLoveTicks() == 0 && !e.isBaby());

                Collections.shuffle(entities);

                if (entities.size() > 1 && extractFeed(TroughBlockEntity.USE_AMOUNT, world, pos, transaction))
                {
                    for (int i = 0; i < Math.min(2, entities.size()); ++i)
                    {
                        AnimalEntity mob = entities.get(i);
                        mob.setBreedingAge(0);
                        mob.lovePlayer(null);
                    }
                    transaction.commit();
                }
                else transaction.abort();
            }
        }
    }

    private boolean extractFeed(long amount, World world, BlockPos pos, TransactionContext transaction)
    {
        if (world.getBlockEntity(pos) instanceof TroughBlockEntity be)
        {
            SingleVariantStorage<FluidVariant> storage = be.getStorage(null);
            return storage.extract(TroughBlockEntity.RESOURCE, amount, transaction) == amount;
        }
        return false;
    }
}
