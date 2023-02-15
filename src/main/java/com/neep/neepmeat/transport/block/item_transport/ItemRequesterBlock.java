package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.item_network.ItemRequester;
import com.neep.neepmeat.transport.api.item_network.RoutingNetwork;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemPipeBlockEntity;
import com.neep.neepmeat.transport.fluid_network.node.NodePos;
import com.neep.neepmeat.transport.item_network.RoutingNetworkDFSFinder;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
public class ItemRequesterBlock extends ItemPipeBlock implements BlockEntityProvider, IItemPipe
{
    public ItemRequesterBlock(String registryName, ItemSettings itemSettings, Settings settings)
    {
        super(registryName, itemSettings, settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient()) world.getBlockEntity(pos, ItemTransport.ITEM_REQUESTER_BE).ifPresent(be ->
        {
            RoutingNetworkDFSFinder finder = new RoutingNetworkDFSFinder(world);
            finder.pushBlock(pos, Direction.UP);
            finder.loop(50);
            if (finder.hasResult())
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    finder.getResult().right().find(null)
                            .request(new ResourceAmount<>(ItemVariant.of(Items.COBBLESTONE), 1), pos, null, RoutingNetwork.RequestType.ANY_AMOUNT, transaction);

                    transaction.commit();
                }
            }
        });
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return ItemTransport.ITEM_REQUESTER_BE.instantiate(pos, state);
    }

    public static class IRBlockEntity extends ItemPipeBlockEntity implements ItemRequester
    {
        public IRBlockEntity(BlockPos pos, BlockState state)
        {
            this(ItemTransport.ITEM_REQUESTER_BE, pos, state);
        }

        public IRBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        @Override
        public long requestItem(ItemVariant variant, long amount, NodePos fromPos, TransactionContext transaction)
        {
            return 0;
        }

        @Override
        public Stream<StorageView<ItemVariant>> getAvailable(TransactionContext transaction)
        {
            return Stream.empty();
        }
    }
}