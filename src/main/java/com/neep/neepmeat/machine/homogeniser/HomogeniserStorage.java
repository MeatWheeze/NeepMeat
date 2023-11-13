package com.neep.neepmeat.machine.homogeniser;

import com.neep.meatlib.util.LazySupplier;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class HomogeniserStorage implements NbtSerialisable
{
    protected final HomogeniserBlockEntity parent;
    protected final WritableStackStorage itemStorage;

    protected final LazySupplier<BlockApiCache<Storage<FluidVariant>, Direction>> fluidInput;
    protected final LazySupplier<BlockApiCache<Storage<FluidVariant>, Direction>> fluidOutput;

    protected final Direction facing;

    public HomogeniserStorage(HomogeniserBlockEntity parent)
    {
        this.parent = parent;
        this.itemStorage = new WritableStackStorage(parent::sync, 64);
        this.facing = parent.getCachedState().get(HomogeniserBlock.FACING);

        fluidInput = LazySupplier.of(() -> BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) parent.getWorld(),
                        parent.getPos().offset(facing.getOpposite())));

        fluidOutput = LazySupplier.of(() -> BlockApiCache.create(FluidStorage.SIDED, (ServerWorld) parent.getWorld(),
                parent.getPos().offset(facing)));
    }

    @Nullable
    public Storage<FluidVariant> getInputFluidStorage()
    {
        return fluidInput.get().find(facing);
    }

    @Nullable
    Storage<FluidVariant> getOutputStorage()
    {
        return fluidOutput.get().find(facing.getOpposite());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        itemStorage.writeNbt(nbt);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        itemStorage.readNbt(nbt);
    }
}
