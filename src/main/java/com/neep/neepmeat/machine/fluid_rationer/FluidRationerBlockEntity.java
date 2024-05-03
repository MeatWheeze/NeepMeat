package com.neep.neepmeat.machine.fluid_rationer;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.screen_handler.FluidRationerScreenHandler;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class FluidRationerBlockEntity extends SyncableBlockEntity implements ExtendedScreenHandlerFactory
{
    protected final FluidRationerStorage outputStorage;
    protected AcceptorModes inMode = AcceptorModes.PULL;
    protected AcceptorModes outMode = AcceptorModes.EXTRACT_ONLY;
    protected final FluidPump outPump = FluidPump.of(-0.1f, this::getOutMode, true);
    protected State state;

    protected BlockApiCache<Storage<FluidVariant>, Direction> cache;

    protected int targetAmount = 81000;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate()
    {
        @Override
        public int get(int index)
        {
            return switch (index)
            {
                case 0 -> targetAmount;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0 -> targetAmount = value;
            }
            markDirty();
        }

        @Override
        public int size()
        {
            return FluidRationerScreenHandler.PROPERTIES;
        }
    };

    public FluidRationerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.outputStorage = new FluidRationerStorage(FluidConstants.BUCKET * 4, this::markDirty);
        this.state = State.IDLE;
    }

    public FluidRationerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.FLUID_RATIONER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, FluidRationerBlockEntity be)
    {
        be.tickInput();
    }

    protected void tickInput()
    {
        if (cache == null)
            updateCache();

        if (cache == null)  // Not sure why this might happen, but who knows.
            return;

        if (state == State.IDLE && world.getTime() % 2 == 0)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                Direction back = getCachedState().get(FluidRationerBlock.FACING).getOpposite();
                Storage<FluidVariant> storage = cache.find(back);

                if (storage == null)
                    return;

                for (StorageView<FluidVariant> view : storage)
                {
                    FluidVariant variant = view.getResource();
                    if (!view.isResourceBlank() && view.getAmount() >= targetAmount && outputStorage.matchesFilter(variant))
                    {
                        long extracted = view.extract(variant, targetAmount, transaction);
                        if (extracted == targetAmount)
                        {
                            if (outputStorage.insert(variant, targetAmount, transaction) == targetAmount)
                            {
                                state = State.PUSHING;
                                inMode = AcceptorModes.NONE;
                                outMode = AcceptorModes.PUSH;
                                transaction.commit();
                                break;
                            }
                        }
                    }
                }
            }
        }
        else if (state == State.PUSHING)
        {
            if (outputStorage.getAmount() == 0)
            {
                state = State.IDLE;
                inMode = AcceptorModes.PULL;
                outMode = AcceptorModes.EXTRACT_ONLY;
            }
        }
    }

    private AcceptorModes getInMode()
    {
        return inMode;
    }

    private AcceptorModes getOutMode()
    {
        return outMode;
    }

    public Storage<FluidVariant> getStorage(Direction direction)
    {
        Direction facing = getCachedState().get(FluidRationerBlock.FACING);
        if (direction == facing)
        {
            return outputStorage;
        }
        return null;
    }

    public FluidPump getPump(Direction direction)
    {
        Direction facing = getCachedState().get(FluidRationerBlock.FACING);
        if (direction == facing)
        {
            return outPump;
        }
        return null;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        outputStorage.toNbt(nbt);
        nbt.putInt("state", state.ordinal());
        nbt.putInt("inMode", inMode.ordinal());
        nbt.putInt("outMode", outMode.ordinal());
        nbt.putInt("targetAmount", targetAmount);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        outputStorage.readNbt(nbt);
        this.state = State.values()[nbt.getInt("state")];
        this.inMode = AcceptorModes.values()[nbt.getInt("inMode")];
        this.outMode = AcceptorModes.values()[nbt.getInt("outMode")];
        this.targetAmount = nbt.getInt("targetAmount");
    }

    public void updateCache()
    {
        if (world instanceof ServerWorld serverWorld)
        {
            BlockPos behind = pos.offset(getCachedState().get(FluidRationerBlock.FACING).getOpposite());
            cache = BlockApiCache.create(FluidStorage.SIDED, serverWorld, behind);
        }
    }

    @Override
    public Text getDisplayName()
    {
        return NMBlocks.FLUID_RATIONER.getName();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new FluidRationerScreenHandler(syncId, inv, outputStorage.inventory, propertyDelegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        buf.writeVarInt(targetAmount);
    }

    public enum State
    {
        IDLE,
        PUSHING
    }
}