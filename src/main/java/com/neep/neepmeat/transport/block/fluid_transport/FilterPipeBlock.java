package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.api.pipe.AbstractAxialFluidPipe;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FilterPipeBlockEntity;
import com.neep.neepmeat.transport.fluid_network.node.BlockPipeVertex;
import com.neep.neepmeat.transport.machine.fluid.FluidPipeBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FilterPipeBlock extends AbstractAxialFluidPipe implements BlockEntityProvider
{
    public FilterPipeBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings, settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState());
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
//            world.updateListeners(pos, state, state, Block.REDRAW_ON_MAIN_THREAD);
            world.scheduleBlockRerenderIfNeeded(pos, state, state);
        }
        return ActionResult.success(world.isClient);
    }

    public static void sendMessage(PlayerEntity player, FluidVariant variant)
    {
        player.sendMessage(Text.translatable("message." + NeepMeat.NAMESPACE + ".filter_pipe.filter", FluidVariantAttributes.getName(variant)), true);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
       return new FilterPipeBlockEntity(pos, state);
    }

    public static class FilterPipeVertex extends BlockPipeVertex
    {
        public FilterPipeVertex(FluidPipeBlockEntity<FilterPipeVertex> fluidPipeBlockEntity)
        {
            super(fluidPipeBlockEntity);
        }

        @Override
        public long canInsert(ServerWorld world, int inDir, FluidVariant variant, long maxAmount)
        {
            if (parent instanceof FilterPipeBlockEntity be)
            {
                return be.getFilterVariant().isBlank() || variant.equals(be.getFilterVariant()) ? maxAmount : 0;
            }
            return 0;
        }

        @Override
        public boolean canSimplify()
        {
            return false;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static int getTint(BlockState state, BlockRenderView world, BlockPos pos, int index)
    {
        if (world.getBlockEntity(pos) instanceof FilterPipeBlockEntity be)
        {
            return FluidVariantRendering.getColor(be.getFilterVariant());
        }
        return 0;
    }
}
