package com.neep.neepmeat.api.big_block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;

public class BigBlockStructureBlockEntity extends BlockEntity
{
    private Vec3i relativePos;
    private BlockPos controllerPos;

    public BigBlockStructureBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.relativePos = new Vec3i(0, 0, 0);
        this.controllerPos = new BlockPos(0, 0, 0);
    }

    public void setController(BlockPos controller)
    {
        controllerPos = controller.toImmutable();
        relativePos = pos.subtract(controller);
    }

    public BlockPos getControllerPos()
    {
        return controllerPos;
    }

    public Vec3i getRelativePos()
    {
        return relativePos;
    }

    public VoxelShape translateShape(VoxelShape outlineShape)
    {
        return outlineShape.offset(-relativePos.getX(), -relativePos.getY(), -relativePos.getZ());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        toVector(nbt.getCompound("relativePos"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.put("relativePos", fromVector(relativePos));
    }

    public static Vec3i toVector(NbtCompound nbt)
    {
        return new Vec3i(nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
    }

    public static NbtCompound fromVector(Vec3i pos)
    {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putInt("X", pos.getX());
        nbtCompound.putInt("Y", pos.getY());
        nbtCompound.putInt("Z", pos.getZ());
        return nbtCompound;
    }

}
