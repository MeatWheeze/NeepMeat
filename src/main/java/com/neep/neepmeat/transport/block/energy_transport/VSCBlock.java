package com.neep.neepmeat.transport.block.energy_transport;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.block.DataCableBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.neepbus.NeepBusPort;
import com.neep.neepmeat.neepbus.NeepBusProvider;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import com.neep.neepmeat.transport.block.energy_transport.entity.VSCBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class VSCBlock extends BaseFacingBlock implements BlockEntityProvider, DataCable, NeepBusProvider
{
    public static final BooleanProperty ACTIVE = BooleanProperty.of("active");

    // Vascular Source Converter!
    public VSCBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(ctx, itemSettings, settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (VascularConduitBlock.matches(player.getMainHandStack()))
            return super.onUse(state, world, pos, player, hand, hit);

        if (player.getStackInHand(hand).isEmpty() && player.isSneaking())
        {
            if (!world.isClient() && world.getBlockEntity(pos) instanceof VSCBlockEntity be)
            {
                be.changeMode();
                world.playSound(null, pos, NMSounds.CLICK, SoundCategory.BLOCKS, 1, 1);
            }
            return ActionResult.SUCCESS;
        }

        if (world.getBlockEntity(pos) instanceof VSCBlockEntity be)
        {
            if (!world.isClient())
                player.openHandledScreen(be);
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.VSC.instantiate(pos, state);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof VSCBlockEntity be)
        {
            be.updateState(world.getReceivedRedstonePower(pos));
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify)
    {
        if (!world.isClient() && world.getBlockEntity(pos) instanceof VSCBlockEntity be)
        {
            be.updateState(world.getReceivedRedstonePower(pos));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(ACTIVE);
    }

    @Override
    public Map<String, NeepBusPort> getPorts(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof VSCBlockEntity be)
        {
            return be.getConfig().getInputPorts();
        }
        return NO_PORTS;
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {
        if (world.getBlockEntity(pos) instanceof VSCBlockEntity be)
        {
            be.invalidatePortCache();
        }
    }
}
