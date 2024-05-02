package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.block.PortBlock;
import com.neep.neepmeat.machine.live_machine.component.FluidOutputComponent;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FluidOutputPortBlockEntity extends SyncableBlockEntity implements FluidOutputComponent
{
    private final WritableSingleFluidStorage storage =  new WritableSingleFluidStorage(16 * FluidConstants.BUCKET, this::markDirty)
    {
//        @Override
//        public boolean supportsInsertion()
//        {
//            return false;
//        }
    };

    private final FluidPump pump = FluidPump.of(-0.5f, () -> AcceptorModes.PUSH, true);

    public FluidOutputPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void setController(BlockPos pos)
    {

    }

    @Override
    public boolean componentRemoved()
    {
        return isRemoved();
    }

    @Override
    public ComponentType<?> getComponentType()
    {
        return LivingMachineComponents.FLUID_OUTPUT;
    }

    @Override
    public Storage<FluidVariant> getStorage(Direction unused)
    {
        return storage;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }

    public FluidPump getPump(Direction direction)
    {
        return direction == getCachedState().get(PortBlock.FACING) ? pump : null;
    }
}
