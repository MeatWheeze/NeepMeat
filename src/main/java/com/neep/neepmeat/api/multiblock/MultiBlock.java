package com.neep.neepmeat.api.multiblock;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface MultiBlock
{
    class Entity extends BlockEntity
    {
        protected BlockPos controllerPos;

        public Entity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {
            super.readNbt(nbt);
            NbtCompound nbt2 = nbt.getCompound("controller");
            if (nbt2 != null)
            {
                setController(NbtHelper.toBlockPos(nbt2));
            }
            else
            {
                setController(null);
            }
        }

        public void writeNbt(NbtCompound nbt)
        {
            super.writeNbt(nbt);
            if (hasController())
            {
                nbt.put("controller", NbtHelper.fromBlockPos(controllerPos));
            }
        }

        public BlockPos getControllerPos()
        {
            return controllerPos;
        }

        public ControllerBlockEntity getController()
        {
            if (getControllerPos() != null && getWorld().getBlockEntity(getControllerPos()) instanceof ControllerBlockEntity be)
            {
                return be;
            }
            return null;
        }

        public void onParentBreak(ServerWorld world)
        {
            if (hasController())
                getController().componentBroken(world);
        }

        public void setController(BlockPos controllerPos)
        {
            this.controllerPos = controllerPos;
            this.markDirty();
        }

        public boolean hasController()
        {
            return getController() != null;
        }

    }
}
