package com.neep.neepmeat.machine.breaker;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class LinearOscillatorBlockEntity extends SyncableBlockEntity implements MotorisedBlock
{
    public static final String NBT_COOLDOWN = "cooldown";
    public static final String NBT_MAX_COOLDOWN = "max_cooldown";

    public static long BASE_WORK_AMOUNT = FluidConstants.BUCKET / 16;

    public static float INCREMENT_MAX = 2;
    public static float INCREMENT_MIN = 0.1f;

    public int cooldownTicks = 40;
    public float cooldown = 0;
    private float cooldownIncrement;

    public float prevExtension = 0f;
    public float extension = 0f;
    public boolean extended = false;
    protected boolean running;

    // Rendering only
    public long extensionTime = 0;
    public float clientExtension = 0;

    public LinearOscillatorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.LINEAR_OSCILLATOR, pos, state);
    }

    public LinearOscillatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void tick()
    {
        INCREMENT_MAX = 2;
        if (!running)
            return;

        cooldown = Math.max(0, cooldown - cooldownIncrement);
        cooldownTicks = 40;

        if (cooldown <= 0)
        {
            extend();
        }
        else
        {
            extended = false;
        }

        prevExtension = extension;
        extension = cooldown / (float) cooldownTicks;
        sync();
    }

    public void extend()
    {
//        if (getWorld().getReceivedRedstonePower(getPos()) <= 0)
//        {
//            return;
//        }

        this.cooldown = this.cooldownTicks;
        this.extended = true;

        Direction facing = getCachedState().get(BaseFacingBlock.FACING);
        BlockPos facingPos = getPos().offset(facing);
        BlockState facingState = world.getBlockState(facingPos);
        float hardness = facingState.getHardness(world, facingPos);

        // Break the block hardness is lower than obsidian
        if (!facingState.isAir() && facingState.getFluidState().isEmpty() && hardness >= 0 && hardness < Blocks.OBSIDIAN.getHardness())
        {
            getWorld().breakBlock(facingPos, true);
        }

        Box box = Box.from(BlockBox.create(facingPos, facingPos));
        List<LivingEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), box, entity -> true);
        entities.forEach(entity ->
        {
            entity.damage(DamageSource.GENERIC, 2);
            double mult = 0.5;
            entity.addVelocity(facing.getOffsetX() * mult, facing.getOffsetY() * mult, facing.getOffsetZ() * mult);
        });

    }

//    public void update(ServerWorld world, BlockPos pos, BlockPos updatePos, BlockState state)
//    {
//        Direction facing = getCachedState().get(LinearOscillatorBlock.FACING);
//        BlockPos backPos = getPos().offset(facing.getOpposite());
//        if (world.getBlockEntity(backPos) instanceof MotorBlockEntity be)
//        {
//            this.connectedMotor = be;
//        }
//        else
//        {
//            this.connectedMotor = null;
//        }
//    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        return false;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.cooldownTicks = nbt.getInt(NBT_MAX_COOLDOWN);
        this.cooldown = nbt.getFloat(NBT_COOLDOWN);
    }

    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt(NBT_MAX_COOLDOWN, cooldownTicks);
        nbt.putFloat(NBT_COOLDOWN, cooldown);
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        this.prevExtension = nbt.getFloat("prev_extension");
        this.extension= nbt.getFloat("extension");

        if (this.cooldown == this.cooldownTicks)
        {
            this.extensionTime = nbt.getLong("world_time");
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        nbt.putLong("world_time", getWorld().getTime());
        nbt.putFloat("prev_extension", prevExtension);
        nbt.putFloat("extension", extension);
        return nbt;
    }

    @Override
    public boolean tick(MotorEntity motor)
    {
        tick();
        return true;
    }

    @Override
    public void setInputPower(float power)
    {
        this.running = power != 0;
        this.cooldownIncrement = INCREMENT_MIN + power * (INCREMENT_MAX - INCREMENT_MIN);
    }
}
