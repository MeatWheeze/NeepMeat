package com.neep.neepmeat.blockentity.machine;

import com.neep.neepmeat.block.machine.HeaterBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.mixin.FurnaceAccessor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeaterBlockEntity extends BloodMachineBlockEntity<HeaterBlockEntity>
{
    public static long USE_AMOUNT = FluidConstants.BUCKET / 300;
    public static long CAPACITY = 4 * USE_AMOUNT;

    protected FurnaceAccessor accessor;

    protected HeaterBlockEntity(BlockEntityType<HeaterBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, CAPACITY, CAPACITY);
    }

    public HeaterBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.HEATER, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, HeaterBlockEntity blockEntity)
    {
        blockEntity.tick();
    }

    public boolean refreshCache(World world, BlockPos pos, BlockState state)
    {
        if (world.getBlockEntity(pos.offset(state.get(HeaterBlock.FACING))) instanceof FurnaceAccessor furnace)
        {
            accessor = furnace;
            return true;
        }
        else
        {
            accessor = null;
            return false;
        }
    }

    public void tick()
    {
        if (accessor == null)
        {
            if (!refreshCache(getWorld(), getPos(), getCachedState()))
            {
                return;
            }
        }

        Transaction transaction = Transaction.openOuter();
        long work = doWork(USE_AMOUNT, transaction);
        if (work == USE_AMOUNT)
        {
            accessor.setBurnTime(2);
        }
        transaction.commit();
    }

    @Override
    public void onUse(PlayerEntity player, Hand hand)
    {
        if (player.isSneaking())
        {
            clearBuffers();
            getWorld().playSound(null, getPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1f, 1f);
        }
        player.sendMessage(Text.of((inputBuffer.getAmount())
                + ", "
                + (outputBuffer.getAmount())), true);
        getWorld().playSound(null, getPos(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1.5f);
    }

    public static void updateBlockState(FurnaceAccessor accessor, World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        state = state.with(AbstractFurnaceBlock.LIT, accessor.getBurnTime() > 0);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
    }
}
