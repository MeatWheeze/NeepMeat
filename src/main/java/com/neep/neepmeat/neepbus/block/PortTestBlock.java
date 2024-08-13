package com.neep.neepmeat.neepbus.block;

import com.google.common.base.Suppliers;
import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.neepbus.*;
import com.neep.neepmeat.neepbus.screen.NeepBusConfigScreenHandler;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PortTestBlock extends BaseBlock implements NeepBusProvider, DataCable, BlockEntityProvider
{
    public PortTestBlock(RegistrationContext ctx, Settings settings)
    {
        super(ctx, settings);
    }

    @Override
    public Map<String, NeepBusPort> getPorts(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos) instanceof PortTestBlockEntity be)
        {
            return be.config.getInputPorts();
        }

        return NO_PORTS;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof PortTestBlockEntity be)
        {
            if (player.isSneaking())
            {
                player.openHandledScreen(new ExtendedScreenHandlerFactory()
                {
                    @Override
                    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
                    {
                        NeepBusConfigScreenHandler.writeOpeningData(be.config, buf);
                    }

                    @Override
                    public Text getDisplayName()
                    {
                        return Text.empty();
                    }

                    @Override
                    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
                    {
                        return new NeepBusConfigScreenHandler(playerInventory, syncId, be.config);
                    }
                });
                return ActionResult.SUCCESS;
            }
            else if (!world.isClient())
            {
                be.send();
                return ActionResult.SUCCESS;
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void networkChanged(World world, BlockPos pos, BlockPos whereChanged)
    {
        if (world.getBlockEntity(pos) instanceof PortTestBlockEntity be)
        {
            be.sender.get().clear();
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NeepBusBlocks.PORT_TEST_BE.instantiate(pos, state);
    }

    public static class PortTestBlockEntity extends SyncableBlockEntity
    {
        private int counter;

        private final AbstractInputPort inputPort = new AbstractInputPort()
        {
            @Override
            public void receive(int data)
            {
                for (PlayerEntity player : getWorld().getPlayers())
                {
                    player.sendMessage(Text.of(String.valueOf(data)), false);
                }
            }
        };

        private final Supplier<CachingSender> sender = Suppliers.memoize(() -> new CachingSender(getWorld(), getPos()));

        private final SimpleOutputPort outputPort = new SimpleOutputPort(new NeepBusConfig.SimpleEntry("brine"), (s, value) -> sender.get().send(s, value));

        private final NeepBusConfig config = new NeepBusConfigImpl(
                List.of(new NeepBusConfig.SimpleEntry("ooer")),
                List.of(outputPort.entry()),
                List.of(inputPort),
                inputPort::invalidateAddress,
                this::markDirty
        );

        public void send()
        {
//            sender.get().send(config.
            outputPort.send(counter);
            counter++;
        }

        public PortTestBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        @Override
        public void writeNbt(NbtCompound nbt)
        {
            super.writeNbt(nbt);
            nbt.put("config", config.writeNbt(new NbtCompound()));
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {
            super.readNbt(nbt);
            this.config.readNbt(nbt.getCompound("config"));
        }
    }
}
