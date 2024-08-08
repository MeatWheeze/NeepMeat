package com.neep.neepmeat.machine.reactor;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.item.BaseBlockItem;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.entity.ActiveWasteFallingBlockEntity;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class ActiveWasteBlock extends FallingBlock implements MeatlibBlock
{
    public ActiveWasteBlock(RegistrationContext ctx, Settings settings)
    {
        super(settings);
        ctx.appendItem(this, new BaseBlockItem(this, ctx, ItemSettings.block()));
    }

    private static void dropStackHere(World world, BlockPos pos, ItemStack stack)
    {
        double d = EntityType.ITEM.getHeight() / 2.0;
        double e = pos.getX() + 0.5;
        double f = pos.getY() + 0.5 - d;
        double g = pos.getZ() + 0.5;
        dropStack(world, () -> new ItemEntity(world, e, f, g, stack, 0, 0, 0), stack);
    }

    private static void dropStack(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack)
    {
        if (!world.isClient && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS))
        {
            ItemEntity itemEntity = itemEntitySupplier.get();
            world.spawnEntity(itemEntity);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        BlockState down = world.getBlockState(pos.down());
        if (down.isOf(LivingMachines.LARGEST_HOPPER) || down.isOf(LivingMachines.LARGEST_HOPPER.getStructure()))
        {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());

            getDroppedStacks(state, world, pos, null, null, ItemStack.EMPTY)
                    .forEach(stack -> dropStackHere(world, pos, stack));
        }
        else
        {
            if (canFallThrough(down) && pos.getY() >= world.getBottomY())
            {
                ActiveWasteFallingBlockEntity fallingBlockEntity = ActiveWasteFallingBlockEntity.spawnFromBlock(world, pos, state);
                this.configureFallingBlockEntity(fallingBlockEntity);
            }
        }
    }

    protected void configureFallingBlockEntity(ActiveWasteFallingBlockEntity entity)
    {
    }
}
