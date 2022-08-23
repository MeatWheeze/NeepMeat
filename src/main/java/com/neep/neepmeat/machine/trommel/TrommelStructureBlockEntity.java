package com.neep.neepmeat.machine.trommel;

import com.neep.neepmeat.api.multiblock.IControllerBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@SuppressWarnings("UnstableApiUsage")
public class TrommelStructureBlockEntity extends BlockEntity
{
    protected BlockPos controllerPos;
    protected TrommelBlockEntity controller;
    protected Direction facing;
    protected Mode mode;

    public TrommelStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TrommelStructureBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TROMMEL_STRUCTURE, pos, state);
    }

    public void setController(BlockPos pos, Direction controllerFacing)
    {
        this.controllerPos = pos;
        this.facing = controllerFacing;
        markDirty();
    }

    public void setMode(Mode mode)
    {
        this.mode = mode;
        markDirty();
    }

    public Storage<FluidVariant> getFluidStorage(Direction direction)
    {
        if (rotateFace(mode.face, facing) == direction)
        {
            if (mode == Mode.FLUID_INPUT)
            {
                return getController().getFluidInput();
            }
            else if (mode == Mode.FLUID_OUTPUT)
            {
                return getController().getFluidOutput();
            }
        }
        return null;
    }

    public Storage<ItemVariant> getItemStorage(Direction direction)
    {
        if (rotateFace(mode.face, facing) == direction)
        {
            if (mode == Mode.ITEM_OUTPUT)
            {
                return getController().getItemOutput();
            }
        }
        return null;
    }

    public static Direction rotateFace(Direction face, Direction facing)
    {
        return switch (facing)
            {
                case EAST -> face.rotateYClockwise();
                case SOUTH -> face.rotateYClockwise().rotateYClockwise();
                case WEST -> face.rotateYClockwise().rotateYClockwise().rotateYClockwise();
                default -> face;
            };
    }

    public void signalBroken(ServerWorld world)
    {
        if (controllerPos != null && world.getBlockEntity(controllerPos) instanceof IControllerBlockEntity be)
        {
            be.componentBroken(world);
        }
    }

    public TrommelBlockEntity getController()
    {
        if (controller == null && controllerPos != null)
        {
            controller = (TrommelBlockEntity) world.getBlockEntity(controllerPos);
        }
        return controller;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.controllerPos = NbtHelper.toBlockPos((NbtCompound) nbt.get("controller"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.put("controller", NbtHelper.fromBlockPos(this.controllerPos));
    }

    public enum Mode
    {
        FLUID_INPUT(Direction.EAST),
        FLUID_OUTPUT(Direction.WEST),
        ITEM_OUTPUT(Direction.WEST),
        MOTOR_PORT(Direction.EAST);
//        WASH_INPUT(Direction.UP),
//        WASH_OUTPUT(Direction.NORTH);

        public Direction face;

        Mode(Direction direction)
        {
            this.face = direction;
        }
    }
}
