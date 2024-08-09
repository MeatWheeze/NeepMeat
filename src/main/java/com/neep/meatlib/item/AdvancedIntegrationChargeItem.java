package com.neep.meatlib.item;

import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.LivingMachineStructure;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.machine.reactor.IntrusionReactor;
import com.neep.neepmeat.machine.reactor.ReceiverOrganismStructure;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class AdvancedIntegrationChargeItem extends BaseItem
{
    public AdvancedIntegrationChargeItem(TooltipSupplier tooltipSupplier, Settings settings)
    {
        super(tooltipSupplier, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(context.getBlockPos());

        if (blockState.getBlock() instanceof ReceiverOrganismStructure)
        {
            if (context.getPlayer() != null)
                context.getPlayer().incrementStat(Stats.USED.getOrCreateStat(this));

            if (!world.isClient())
            {
                boolean success = findController(world, context.getBlockPos(),
                        found ->
                        {
                            if (found instanceof IntegratorBlockEntity be)
                            {
                                world.setBlockState(be.getPos(), IntrusionReactor.REACTION_CORE.getDefaultState());

                                world.playSound(null, context.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 1, 1);
                                ItemStack newStack = context.getStack().copy();
                                newStack.decrement(1);
                                context.getPlayer().setStackInHand(context.getHand(), newStack);
                                return true;
                            }
                            return false;
                        },
                        s -> s.getBlock() instanceof ReceiverOrganismStructure, 64);
            }

            return ActionResult.SUCCESS;
        }

        if (blockState.getBlock() instanceof LivingMachineStructure)
        {
            if (context.getPlayer() != null)
                context.getPlayer().incrementStat(Stats.USED.getOrCreateStat(this));

            if (!world.isClient())
            {
                boolean success = findController(world, context.getBlockPos(),
                    found ->
                    {
                        if (found instanceof LivingMachineBlockEntity be)
                        {
                            be.getDegradationManager().subtract(0.5f);
                            world.playSound(null, context.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 1, 1);
                            ItemStack newStack = context.getStack().copy();
                            newStack.decrement(1);
                            context.getPlayer().setStackInHand(context.getHand(), newStack);
                            return true;
                        }
                        return false;
                    },
                    s -> s.getBlock() instanceof LivingMachineStructure, 64);
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Nullable
    private static <T extends BlockEntity> boolean findController(World world, BlockPos origin, Function<BlockEntity, Boolean> function, Predicate<BlockState> isStructure, int maxStructures)
    {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(origin);
        visited.add(origin);

        int structuresVisited = 0;

        while (!queue.isEmpty() && structuresVisited < maxStructures)
        {
            BlockPos current = queue.poll();

            BlockPos.Mutable mutable = current.mutableCopy();

            for (Direction direction : Direction.values())
            {
                mutable.set(current, direction);

                if (!visited.contains(mutable))
                {
                    visited.add(mutable.toImmutable());

                    BlockState nextState = world.getBlockState(mutable);

                    BlockEntity be;
                    if (nextState.isAir())
                    {
                        continue;
                    }
                    else if (isStructure.test(nextState))
                    {
                        queue.add(mutable.toImmutable());
                        structuresVisited++;
                    }
                    else if ((be = world.getBlockEntity(mutable)) != null)
                    {
                        if (function.apply(be))
                            return true;
                    }
                }
            }
        }

        return false;
    }
}
