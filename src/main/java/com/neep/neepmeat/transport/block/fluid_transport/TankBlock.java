package com.neep.neepmeat.transport.block.fluid_transport;

import com.google.common.collect.Lists;
import com.neep.meatlib.block.BaseColumnBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.machine.fluid.TankBlockEntity;
import com.neep.neepmeat.util.ItemUtil;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantItemStorage;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TankBlock extends BaseColumnBlock implements BlockEntityProvider, BlockPickInteractionAware
{
    public TankBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(ctx, itemSettings.maxCount(1), settings);
    }

    @Override
    public Item asItem()
    {
        return super.asItem();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.TANK.instantiate(pos, state);
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state)
    {
        return new ItemStack(this);
    }

    @Override
    public ItemStack getPickedStack(BlockState state, BlockView view, BlockPos pos, PlayerEntity player, HitResult result)
    {
        if (player.isCreative())
        {
            ItemStack stack = super.getPickStack(view, pos, state);
            if (view.getBlockEntity(pos) instanceof TankBlockEntity be)
            {
                if (!be.getStorage(null).isResourceBlank())
                {
                    be.setStackNbt(stack);
                }
            }
            return stack;
        }
        else
        {
            if (view.getBlockEntity(pos) instanceof TankBlockEntity be)
            {
                FluidVariant storedVariant = be.getStorage(null).getResource();

                // Allow picking an empty tank in survival mode. Prioritise stacks with the same fluid.
                Item asItem = this.asItem();
                List<ItemStack> matching = Lists.newArrayList();
                List<ItemStack> matchingFluid = Lists.newArrayList();
                for (int i = 0; i < player.getInventory().size(); ++i)
                {
                    ItemStack stack = player.getInventory().getStack(i);
                    if (stack.isOf(asItem))
                    {
                        matching.add(stack);

                        NbtCompound stackNbt = stack.getSubNbt("BlockEntityTag");
                        if (stackNbt != null)
                        {
                            FluidVariant fluidVariant = WritableSingleFluidStorage.readFluidVariant(stackNbt);
                            if (fluidVariant.getFluid() == storedVariant.getFluid())
                                matchingFluid.add(stack);
                        }
                    }
                }

                if (!matchingFluid.isEmpty())
                {
                    return matchingFluid.get(0);
                }
                else if (!matching.isEmpty())
                {
                    return matching.get(0);
                }
            }

            return getPickStack(view, pos, state);
        }
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder)
    {
        List<ItemStack> stacks = super.getDroppedStacks(state, builder);
        BlockEntity blockEntity = builder.get(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof TankBlockEntity be)
        {
            if (!be.getStorage(null).isResourceBlank())
                stacks.forEach(be::setStackNbt);

            return stacks;
        }
        return stacks;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtil.playerHoldingPipe(player, hand))
            return ActionResult.PASS;

        if (world.getBlockEntity(pos) instanceof TankBlockEntity tank && tank.onUse(player, hand))
        {
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static ItemApiLookup.ItemApiProvider<Storage<FluidVariant>, ContainerItemContext> createStorageProvider(long capacity)
    {
        return (stack, ctx) -> new TankItemFluidStorage(stack, ctx, capacity);
    }

    public static class TankItemFluidStorage extends SingleVariantItemStorage<FluidVariant>
    {
        protected final ItemStack stack;
        protected final ContainerItemContext context;
        private final long capacity;

        public TankItemFluidStorage(ItemStack stack, ContainerItemContext context, long capacity)
        {
            super(context);
            this.stack = stack;
            this.context = context;
            this.capacity = capacity;
        }

        @Override
        protected FluidVariant getBlankResource()
        {
            return FluidVariant.blank();
        }

        @Nullable
        private NbtCompound getBlockEntityTag(ItemVariant itemVariant)
        {
            NbtCompound nbt = itemVariant.getNbt();
            if (nbt == null || !nbt.contains("BlockEntityTag"))
            {
                return null;
            }
            return nbt.getCompound("BlockEntityTag");
        }

        @Override
        protected FluidVariant getResource(ItemVariant currentVariant)
        {
            NbtCompound nbt = getBlockEntityTag(currentVariant);
            if (nbt == null)
                return FluidVariant.blank();

            return WritableSingleFluidStorage.readFluidVariant(nbt);
        }

        @Override
        protected long getAmount(ItemVariant currentVariant)
        {
            NbtCompound nbt = getBlockEntityTag(currentVariant);
            if (nbt == null)
                return 0;

            return WritableSingleFluidStorage.readAmount(nbt);
        }

        @Override
        protected long getCapacity(FluidVariant variant)
        {
//            NbtCompound nbt = getBlockEntityTag(ItemVariant.of(stack));
//            if (nbt == null)
//                return 0;
//
//            return WritableSingleFluidStorage.readCapacity(nbt);
            return capacity;
        }

        @Override
        protected ItemVariant getUpdatedVariant(ItemVariant currentVariant, FluidVariant newResource, long newAmount)
        {
            ItemStack stack = currentVariant.toStack();

            if (newResource.isBlank() || newAmount == 0)
            {
                stack.removeSubNbt("BlockEntityTag");
                return ItemVariant.of(stack);
            }

            NbtCompound subNbt = stack.getOrCreateSubNbt("BlockEntityTag");

            subNbt.put(WritableSingleFluidStorage.KEY_RESOURCE, newResource.toNbt());
            subNbt.putLong(WritableSingleFluidStorage.KEY_AMOUNT, newAmount);

            return ItemVariant.of(stack);
        }
    }
}
