package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.transport.api.pipe.AbstractAxialPipe;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FilterPipeBlockEntity;
import com.neep.neepmeat.transport.fluid_network.PipeState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FilterPipeBlock extends AbstractAxialPipe implements PipeState.ISpecialPipe, BlockEntityProvider
{
    public FilterPipeBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, FluidComponentItem::new, settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState());
    }

    @Override
    public PipeState.FilterFunction getFlowFunction(World world, Direction bias, BlockPos pos, BlockState state)
    {
        FilterPipeBlockEntity be = world.getBlockEntity(pos, NMBlockEntities.FILTER_PIPE).orElse(null);
        if (be != null)
        {
            return (v, a) -> be.getFilterVariant().isBlank() || v.equals(be.getFilterVariant()) ? a : 0;
        }
        return PipeState::identity;
    }

    @Override
    public boolean canTransferFluid(Direction bias, BlockState state)
    {
        return true;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (world.isClient())
            return;

        if (!state.isOf(newState.getBlock()))
        {
            removePipe((ServerWorld) world, state, pos);
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        FilterPipeBlockEntity be = world.getBlockEntity(pos, NMBlockEntities.FILTER_PIPE).orElse(null);
        if (be == null)
        {
            NeepMeat.LOGGER.error("Filter pipe block entity has been removed.");
        }
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient() && be != null)
        {
            Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
            if (player.isSneaking())
            {
                // Clear filter
                be.setFilterFluid(FluidVariant.blank());
            }
            if (storage != null)
            {
                // Try to set filter
                try (Transaction transaction = Transaction.openOuter())
                {
                    FluidVariant variant = StorageUtil.findExtractableResource(storage, transaction);

                    if (variant != null && !variant.isBlank())
                    {
                        be.setFilterFluid(variant);
                    }
                    transaction.commit();
                }
            }
            sendMessage(player, be.getFilterVariant());
        }
        if (world.isClient())
        {
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
        }
        return ActionResult.success(world.isClient);
    }

    public static void sendMessage(PlayerEntity player, FluidVariant variant)
    {
        player.sendMessage(new TranslatableText("message." + NeepMeat.NAMESPACE + ".filter_pipe.filter", FluidVariantAttributes.getName(variant)), true);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
       return new FilterPipeBlockEntity(pos, state);
    }

    @Environment(value=EnvType.CLIENT)
    public static int getTint(BlockState state, BlockRenderView world, BlockPos pos, int index)
    {
        if (world.getBlockEntity(pos) instanceof FilterPipeBlockEntity be)
        {
            int c = FluidVariantRendering.getColor(be.getFilterVariant());
            return c;
        }
        return 0;
    }
}
