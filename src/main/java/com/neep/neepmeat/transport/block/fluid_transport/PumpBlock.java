package com.neep.neepmeat.transport.block.fluid_transport;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.blockentity.fluid.PumpBlockEntity;
import com.neep.neepmeat.blockentity.fluid.TankBlockEntity;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import com.neep.neepmeat.transport.fluid_network.PipeNetwork;
import com.neep.neepmeat.transport.fluid_network.node.FluidNode;
import com.neep.neepmeat.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.function.Supplier;

public class PumpBlock extends BaseFacingBlock implements BlockEntityProvider, IDirectionalFluidAcceptor, IFluidNodeProvider
{
    public PumpBlock(String itemName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(itemName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1.0f, 1f);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (world.getBlockEntity(pos) instanceof PumpBlockEntity be)
        {
            boolean powered = world.isReceivingRedstonePower(pos);
            be.setActive(powered);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new PumpBlockEntity(pos, state);
    }

    @Override
    public boolean connectInDirection(World world, BlockPos pos, BlockState state, Direction direction)
    {
        return state.get(FACING).equals(direction) || state.get(FACING).getOpposite().equals(direction);
    }

    @Override
    public AcceptorModes getDirectionMode(World world, BlockPos pos, BlockState state, Direction direction)
    {
        if (world.getBlockEntity(pos) instanceof PumpBlockEntity pump)
        {
            if (direction == state.get(FACING))
            {
                return pump.frontMode;
            }
            if (direction == state.get(FACING).getOpposite())
            {
                return pump.backMode;
            }
        }
        return AcceptorModes.NONE;
    }

    @Override
    public boolean isStorage()
    {
        return true;
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtils.playerHoldingPipe(player, hand))
            return ActionResult.PASS;

        PumpBlockEntity be = (PumpBlockEntity) world.getBlockEntity(pos);
        if (!world.isClient)
        {
            if (!player.isSneaking())
            {
//                be.update(state, world);
                HashSet<Supplier<FluidNode>> test = new HashSet<>();

//                NodePos pos1 = new NodePos(new BlockPos(1, 2, 3), Direction.NORTH);
//                NodePos pos2 = new NodePos(new BlockPos(1, 2, 3), Direction.NORTH);
//                FluidNetwork.NodeSupplier supplier1 = new FluidNetwork.NodeSupplier(pos1);
//                FluidNetwork.NodeSupplier supplier2 = new FluidNetwork.NodeSupplier(pos2);
//
//                test.add(supplier1);
//
//                System.out.println(test.contains(supplier2));

//                NMFluidNetwork.LOADED_NETWORKS.forEach(NMFluidNetwork::tick);
            }
            else
            {
//                System.out.println(NMFluidNetwork.LOADED_NETWORKS);
                for (PipeNetwork network : PipeNetwork.LOADED_NETWORKS)
                {
//                    System.out.println(network.connectedNodes);
                    System.out.print("\n" + network.uid + " nodes: ");
                    for (Supplier<FluidNode> supplier : network.connectedNodes)
                    {
                        System.out.print(supplier.get());
                    }
                    System.out.print("\n");
                }
            }
            TankBlockEntity.showContents((ServerPlayerEntity) player, world, pos, be.getBuffer(null));
        }
        return ActionResult.SUCCESS;
    }
}
