package com.neep.neepmeat.neepbus.block;

import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.neepbus.NeepBusBlocks;
import com.neep.neepmeat.neepbus.NeepBusPort;
import com.neep.neepmeat.neepbus.NeepBusProvider;
import com.neep.neepmeat.neepbus.SimpleScreenHandlerFactory;
import com.neep.neepmeat.neepbus.block.entity.LinearLeverBlockEntity;
import com.neep.neepmeat.neepbus.screen.InteractiveControlScreenHandler;
import com.neep.neepmeat.neepbus.screen.NeepBusScreenHandler;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class LinearLeverBlock extends WallMountedBlock implements BlockEntityProvider, NeepBusProvider, DataCable
{
    public static final VoxelShape NORTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 10.0, 12.0, 15.0, 16.0);
    public static final VoxelShape SOUTH_WALL_SHAPE = Block.createCuboidShape(4.0, 1.0, 0.0, 12.0, 15.0, 6.0);
    public static final VoxelShape WEST_WALL_SHAPE = Block.createCuboidShape(10.0, 1.0, 4.0, 16.0, 15.0, 12.0);
    public static final VoxelShape EAST_WALL_SHAPE = Block.createCuboidShape(0.0, 1.0, 4.0, 6.0, 15.0, 12.0);
    public static final VoxelShape FLOOR_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 0.0, 1.0, 12.0, 6.0, 15.0);
    public static final VoxelShape FLOOR_X_AXIS_SHAPE = Block.createCuboidShape(1.0, 0.0, 4.0, 15.0, 6.0, 12.0);
    public static final VoxelShape CEILING_Z_AXIS_SHAPE = Block.createCuboidShape(4.0, 10.0, 1.0, 12.0, 16.0, 15.0);
    public static final VoxelShape CEILING_X_AXIS_SHAPE = Block.createCuboidShape(1.0, 10.0, 4.0, 15.0, 16.0, 12.0);

    public LinearLeverBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(settings);
        itemSettings.create(this, ctx, itemSettings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof LinearLeverBlockEntity be)
        {
            if (!player.isSneaking())
            {
                player.openHandledScreen((SimpleScreenHandlerFactory) (syncId, playerInventory, player1) ->
                        new InteractiveControlScreenHandler(syncId, playerInventory, be::onScroll));
            }
            else
            {
                player.openHandledScreen(new ExtendedScreenHandlerFactory()
                {
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
                    {
                        NeepBusScreenHandler.writeOpeningData(be.getConfig(), buf);
                    }

                    @Override
                    public Text getDisplayName()
                    {
                        return Text.empty();
                    }

                    @Nullable
                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
                    {
                        return new NeepBusScreenHandler(playerInventory, syncId, be.getConfig());
                    }
                });
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NeepBusBlocks.LINEAR_LEVER_BE.instantiate(pos, state);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        switch (state.get(FACE))
        {
            case FLOOR ->
            {
                return switch ((state.get(FACING)).getAxis())
                {
                    case X -> FLOOR_X_AXIS_SHAPE;
                    default -> FLOOR_Z_AXIS_SHAPE;
                };
            }
            case WALL ->
            {
                return switch (state.get(FACING))
                {
                    case EAST -> EAST_WALL_SHAPE;
                    case WEST -> WEST_WALL_SHAPE;
                    case SOUTH -> SOUTH_WALL_SHAPE;
                    default -> NORTH_WALL_SHAPE;
                };
            }
            default ->
            {
                return switch (state.get(FACING).getAxis())
                {
                    case X -> CEILING_X_AXIS_SHAPE;
                    default -> CEILING_Z_AXIS_SHAPE;
                };
            }
        }
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FACE, FACING);
    }

    @Override
    public Map<String, NeepBusPort> getPorts(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof LinearLeverBlockEntity be)
        {
            return be.getPorts();
        }
        return NO_PORTS;
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {
        if (world.getBlockEntity(pos) instanceof LinearLeverBlockEntity be)
        {
            be.updateNetwork();
        }
    }
}
