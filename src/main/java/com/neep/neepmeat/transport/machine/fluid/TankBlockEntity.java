package com.neep.neepmeat.transport.machine.fluid;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.storage.FluidBuffer;
import com.neep.neepmeat.api.storage.WritableFluidBuffer;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.network.TankMessagePacket;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class TankBlockEntity extends SyncableBlockEntity
{
    protected final WritableSingleFluidStorage buffer;

    public TankBlockEntity(BlockEntityType type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.buffer = new WritableSingleFluidStorage(8 * FluidConstants.BUCKET, this::sync);
    }

    public TankBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TANK_BLOCK_ENTITY, pos, state);
    }

    public WritableSingleFluidStorage getStorage(Direction direction)
    {
        return buffer;
    }

    @Override
    public void writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNbt(tag);
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        if (WritableFluidBuffer.handleInteract(buffer, world, player, hand))
        {
            return true;
        }
        else if (!world.isClient())
        {
            showContents((ServerPlayerEntity) player, world, getPos(), buffer);
            return true;
        }
        return true;
    }

    public static void showContents(ServerPlayerEntity player, World world, BlockPos pos, StorageView<FluidVariant> buffer)
    {
        world.playSound(null, pos, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1.5f);
        TankMessagePacket.send(player, pos, buffer.getAmount(), buffer.getResource());
    }
}
