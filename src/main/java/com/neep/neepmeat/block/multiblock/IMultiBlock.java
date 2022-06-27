package com.neep.neepmeat.block.multiblock;

import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.system.CallbackI;

public interface IMultiBlock
{
    class Entity extends BlockEntity
    {
        protected BlockPos controllerPos;

        public Entity(BlockPos pos, BlockState state)
        {
            this(NMBlockEntities.VAT_CASING, pos, state);
        }

        public Entity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        public static FabricBlockEntityTypeBuilder.Factory<Entity> createFactory(BlockEntityType<?> type)
        {
            return (pos, state) -> new Entity(type, pos, state);
        }

        public BlockPos getControllerPos()
        {
            return controllerPos;
        }

        public IControllerBlockEntity getController()
        {
            if (getControllerPos() != null && getWorld().getBlockEntity(getControllerPos()) instanceof IControllerBlockEntity be)
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
        }

        public boolean hasController()
        {
            return getController() != null;
        }

    }
}
