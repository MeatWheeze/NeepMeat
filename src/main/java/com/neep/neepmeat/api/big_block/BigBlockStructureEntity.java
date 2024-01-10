package com.neep.neepmeat.api.big_block;

import com.google.common.collect.Lists;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BigBlockStructureEntity extends SyncableBlockEntity
{
    @Nullable
    protected BlockPos controllerPos;
    protected List<Identifier> apis = Lists.newArrayList();

    public BigBlockStructureEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void setController(@Nullable BlockPos pos)
    {
        this.controllerPos = pos;
    }

    public void enableApi(BlockApiLookup<?, ?> api)
    {
        apis.add(api.getId());
    }

//    public void setApi(BlockApiLookup<>)

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

        if (!apis.isEmpty())
        {
            NbtList list = new NbtList();
            for (var id : apis)
            {
                list.add(NbtString.of(id.toString()));
            }
            nbt.put("apis", list);
        }
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

        apis.clear();
        if (nbt.contains("apis"))
        {
            NbtList list = nbt.getList("apis", NbtElement.STRING_TYPE);
            for (int i = 0; i < list.size(); ++i)
            {
                String string = list.getString(i);
                apis.add(Identifier.tryParse(string));
            }
        }
    }

    @Nullable
    public BlockPos getControllerPos()
    {
        return controllerPos;
    }
}
