package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.machine.HeaterBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.mixin.FurnaceAccessor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public class HeaterBlockEntity extends BloodMachineBlockEntity
{
    public static long USE_AMOUNT = FluidConstants.BUCKET / 300;
    public static long CAPACITY = 4 * USE_AMOUNT;

    protected FurnaceAccessor accessor;
    protected int copperTime;

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
            refreshCache(getWorld(), getPos(), getCachedState());
        }

        Transaction transaction = Transaction.openOuter();
        long work = doWork(USE_AMOUNT, transaction);
        if (work == USE_AMOUNT)
        {
            if (accessor != null)
                accessor.setBurnTime(2);

            heatBlock();
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

    public void heatBlock()
    {
        BlockPos facingPos = getPos().offset(getCachedState().get(BaseFacingBlock.FACING));
        BlockState facingState = world.getBlockState(facingPos);
        if (facingState.getBlock() instanceof OxidizableBlock)
        {
            ++copperTime;
//            getWorld().addParticle(ParticleTypes.LAVA, facingPos.getX() + 0.5, facingPos.getY() + 1.5, facingPos.getZ() + 0.5, 0, 0, 0);
            if (copperTime == 100)
            {
                copperTime = 0;
                Optional<Block> nextBlock = Oxidizable.getIncreasedOxidationBlock(facingState.getBlock());
                if (canOxidise(world, facingPos) && nextBlock.isPresent())
                {
                    world.setBlockState(facingPos, nextBlock.get().getDefaultState());
                }
            }
        }
        else
        {
            copperTime = 0;
        }
    }

    public static boolean canOxidise(World world, BlockPos pos)
    {
        for (Direction direction : Direction.values())
        {
            BlockPos offset = pos.offset(direction);
            if (world.getBlockState(offset).isOf(NMFluids.PATINA_TREATMENT))
            {
                return true;
            }
        }
        return false;
    }

    public void checkPatinaState(World world, BlockPos copperPos)
    {
        if (world.getBlockState(copperPos).isOf(Blocks.COPPER_BLOCK))
        {

        }
    }
}
