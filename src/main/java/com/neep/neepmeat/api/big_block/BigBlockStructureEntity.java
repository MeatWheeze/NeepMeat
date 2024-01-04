package com.neep.neepmeat.api.big_block;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public class BigBlockStructureEntity extends SyncableBlockEntity
{
    @Nullable
    private BlockPos controllerPos;

    public BigBlockStructureEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void setController(@Nullable BlockPos pos)
    {
        this.controllerPos = pos;
    }

    public VoxelShape translateShape(VoxelShape outlineShape)
    {
        if (controllerPos == null)
            return VoxelShapes.fullCube();

        BlockPos relative = pos.subtract(controllerPos);
        return outlineShape.offset(-relative.getX(), -relative.getY(), -relative.getZ());
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        if (controllerPos != null)
            nbt.put("controller_pos", NbtHelper.fromBlockPos(controllerPos));
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        if (nbt.contains("controller_pos"))
        {
            this.controllerPos = NbtHelper.toBlockPos(nbt.getCompound("controller_pos"));
        }
        else
            this.controllerPos = null;
    }

    @Nullable
    public BlockPos getControllerPos()
    {
        return controllerPos;
    }
}
