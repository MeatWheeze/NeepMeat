package com.neep.neepmeat.blockentity.fluid;

import com.neep.neepmeat.fluid_transfer.FluidBuffer;
import com.neep.neepmeat.fluid_transfer.storage.WritableFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.network.TankMessagePacket;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class TankBlockEntity extends BlockEntity implements com.neep.neepmeat.fluid_transfer.FluidBuffer.FluidBufferProvider
{
    protected final WritableFluidBuffer buffer;

    public TankBlockEntity(BlockEntityType type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.buffer = new WritableFluidBuffer(this, 8 * FluidConstants.BUCKET);
    }

    public TankBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.TANK_BLOCK_ENTITY, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        buffer.writeNBT(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
        buffer.readNBT(tag);
    }

    @Override
    @Nullable
    public WritableFluidBuffer getBuffer(Direction direction)
    {
        return buffer;
    }

    @Override
    public void setNeedsUpdate(boolean needsUpdate)
    {

    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);
        Storage<FluidVariant> storage = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
        if (storage != null)
        {
            if (StorageUtil.move(storage, getBuffer(null), variant -> true, Long.MAX_VALUE, null) > 0)
                return true;

            if (StorageUtil.move(getBuffer(null), storage, variant -> true, Long.MAX_VALUE, null) > 0)
                return true;
        }

        else if (!world.isClient())
        {
            showContents((ServerPlayerEntity) player, getPos(), buffer);
            return true;
        }
        return true;
    }

    public static void showContents(ServerPlayerEntity player, BlockPos pos, FluidBuffer buffer)
    {
        TankMessagePacket.send(player, pos, buffer.getAmount(), buffer.getResource());
    }
}
