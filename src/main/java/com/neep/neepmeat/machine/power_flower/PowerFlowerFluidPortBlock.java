package com.neep.neepmeat.machine.power_flower;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.item.ItemSettings;
import com.neep.neepmeat.api.FluidPump;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.transport.fluid_network.node.AcceptorModes;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class PowerFlowerFluidPortBlock extends BaseBlock implements BlockEntityProvider, PowerFlower
{
    public PowerFlowerFluidPortBlock(String registryName, ItemSettings block, Settings settings)
    {
        super(registryName, block, settings.nonOpaque());
    }

    @Override
    public TagKey<Block> getPreferredTool()
    {
        return BlockTags.AXE_MINEABLE;
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context)
    {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.POWER_FLOWER_FLUID_PORT.instantiate(pos, state);
    }

    public static class PFPortBlockEntity extends SyncableBlockEntity
    {
        @Nullable private BlockPos controllerPos;
        @Nullable private BlockApiCache<Void, Void> cache;

        private final FluidPump fluidPump = FluidPump.of(0.5f, () -> AcceptorModes.PULL, true);

        public PFPortBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
        {
            super(type, pos, state);
        }

        @Override
        public void writeNbt(NbtCompound nbt)
        {
            super.writeNbt(nbt);
            if (controllerPos != null)
                nbt.put("parent_pos", NbtHelper.fromBlockPos(controllerPos));
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {
            super.readNbt(nbt);
            if (nbt.contains("parent_pos"))
                this.controllerPos = NbtHelper.toBlockPos(nbt.getCompound("parent_pos"));
        }

        @Nullable
        public Storage<FluidVariant> getStorage(Direction direction)
        {
            var controller = getControllerBE();
            if (controller != null)
            {
                return controller.getFluidStorage();
            }
            return Storage.empty();
        }

        @Nullable
        public FluidPump getPump(Direction direction)
        {
            return fluidPump;
        }

        @Nullable
        private PowerFlowerControllerBlockEntity getControllerBE()
        {
            if (controllerPos == null)
            {
                cache = null;
                return null;
            }
            else if (cache == null)
            {
                cache = BlockApiCache.create(MeatLib.VOID_LOOKUP, (ServerWorld) getWorld(), controllerPos);
            }

            if (cache.getBlockEntity() instanceof PowerFlowerControllerBlockEntity controller)
            {
                return controller;
            }
            else
            {
                // Invalidate of the controller has been removed.
                controllerPos = null;
                cache = null;
                return null;
            }
        }

        public void setController(BlockPos controller)
        {
            this.controllerPos = controller;
            world.updateNeighbors(getPos(), getCachedState().getBlock());
        }
    }
}
