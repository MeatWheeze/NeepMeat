package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.block.BaseColumnBlock;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.item.TankItem;
import com.neep.neepmeat.transport.machine.fluid.TankBlockEntity;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.util.ItemUtils;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TankBlock extends BaseColumnBlock implements BlockEntityProvider
{
    public TankBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, TankItem::new, settings.nonOpaque());
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
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> stacks = super.getDroppedStacks(state, builder);
        BlockEntity blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof TankBlockEntity be)
        {
//            LootContext lootContext = builder.parameter(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.BLOCK);
//            ServerWorld serverWorld = lootContext.getWorld();
//            LootTable lootTable = serverWorld.getServer().getLootManager().getTable(this.getLootTableId());
//            List<ItemStack> stacks = lootTable.generateLoot(lootContext);

            if (!be.getStorage(null).isResourceBlank()) stacks.forEach(be::setStackNbt);

            return stacks;
        }
        return stacks;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtils.playerHoldingPipe(player, hand))
            return ActionResult.PASS;

        if (world.getBlockEntity(pos) instanceof TankBlockEntity tank && tank.onUse(player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
