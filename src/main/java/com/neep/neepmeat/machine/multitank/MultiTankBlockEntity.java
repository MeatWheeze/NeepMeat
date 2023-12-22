package com.neep.neepmeat.machine.multitank;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.transfer.MultiFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.network.TankMessagePacket;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiTankBlockEntity extends SyncableBlockEntity
{
    protected MultiFluidBuffer buffer;

    public MultiTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.buffer = new MultiFluidBuffer(8 * FluidConstants.BUCKET, variant -> true, this::sync);
    }

    public MultiTankBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MULTI_TANK, pos, state);
    }

    public MultiFluidBuffer getStorage()
    {
        return buffer;
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        if (buffer.handleInteract(world, player, hand))
        {
            return true;
        }
        if (!world.isClient())
        {
            showContents((ServerPlayerEntity) player, world, getPos(), getStorage());
            return true;
        }
        return true;
    }

    public static void showContents(ServerPlayerEntity player, World world, BlockPos pos, MultiFluidBuffer buffer)
    {
        world.playSound(null, pos, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1.5f);
        long mb = Math.floorDiv(buffer.getTotalAmount(), FluidConstants.BUCKET / 1000);
        player.sendMessage(Text.of("Fluid" + ": " + mb + "mb"), true);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        buffer.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        buffer.readNbt(nbt);
    }

}