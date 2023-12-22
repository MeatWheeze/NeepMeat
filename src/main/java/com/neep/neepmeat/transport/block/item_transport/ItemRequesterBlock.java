package com.neep.neepmeat.transport.block.item_transport;

import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.transport.ItemTransport;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.transport.block.item_transport.entity.ItemRequesterBlockEntity;
import com.neep.neepmeat.util.MiscUtils;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
//        if (!world.isClient()) world.getBlockEntity(pos, ItemTransport.ITEM_REQUESTER_BE).ifPresent(be ->
//        {
//            RoutingNetworkDFSFinder finder = new RoutingNetworkDFSFinder(world);
//            finder.pushBlock(pos, Direction.UP);
//            finder.loop(50);
//            if (finder.hasResult())
//            {
//                try (Transaction transaction = Transaction.openOuter())
//                {
//                    finder.getResult().right().find(null)
//                            .request(new ResourceAmount<>(ItemVariant.of(Items.COBBLESTONE), 1), pos, null, RoutingNetwork.RequestType.ANY_AMOUNT, transaction);
//
//                    transaction.commit();
//                }
//            }
//        });
        if (world.getBlockEntity(pos) instanceof ItemRequesterBlockEntity be)
        {
            player.openHandledScreen(be);
            return ActionResult.SUCCESS;
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, ItemTransport.ITEM_REQUESTER_BE, ItemRequesterBlockEntity::serverTick, null, world);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return ItemTransport.ITEM_REQUESTER_BE.instantiate(pos, state);
    }

}