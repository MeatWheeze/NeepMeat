package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.item.FluidComponentItem;
import com.neep.neepmeat.transport.machine.fluid.PumpBlockEntity;
import com.neep.neepmeat.transport.machine.fluid.TankBlockEntity;
import com.neep.neepmeat.util.ItemUtil;
import com.neep.neepmeat.util.MiscUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PumpBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public PumpBlock(String itemName, ItemSettings itemSettings, Settings settings)
    {
        super(itemName, itemSettings.factory(FluidComponentItem::new), settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1.0f, 1f);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        if (world.getBlockEntity(pos) instanceof PumpBlockEntity be)
        {
            boolean powered = world.isReceivingRedstonePower(pos);
            be.setActive(powered);
            be.updateCache();
//            world.updateNeighbors(pos, this);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (world.getBlockEntity(pos) instanceof PumpBlockEntity be)
        {
            be.updateCache();
        }
    }

    @Override
    public void prepare(BlockState state, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth)
    {
        super.prepare(state, world, pos, flags, maxUpdateDepth);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.PUMP.instantiate(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtil.playerHoldingPipe(player, hand))
            return ActionResult.PASS;

        PumpBlockEntity be = (PumpBlockEntity) world.getBlockEntity(pos);
        if (!world.isClient)
        {
            if (!player.isSneaking())
            {
            }
            else
            {
//                System.out.println("All ticking fluid networks:");
//                for (PipeNetwork network : PipeNetwork.LOADED_NETWORKS)
//                {
////                    System.out.println(network.connectedNodes);
//                    System.out.print("\n" + network.getUUID() + " nodes: ");
////                    if (network instanceof PipeNetworkImpl1 impl1)
////                    {
////                        for (Supplier<FluidNode> supplier : impl1.getNodes())
////                        {
////                            System.out.print(supplier.get());
////                        }
////                    }
//                    if (network instanceof PipeNetworkImpl impl2)
//                    {
//                        System.out.print("\n");
//                        impl2.getGraph().getVertices().forEach((k, v) -> System.out.print(BlockPos.fromLong(k) + ": " + v.toString() + "\n"));
//                    }
//                    System.out.print("\n");
//                }
            }
            TankBlockEntity.showContents((ServerPlayerEntity) player, world, pos, be.getBuffer(null));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtil.checkType(type, NMBlockEntities.PUMP, (world1, pos, state1, blockEntity) -> blockEntity.tick(), null, world);
    }

}
