package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.block.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import com.neep.neepmeat.machine.motor.MotorBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class LinearOscillatorBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    public static final String NBT_COOLDOWN = "cooldown";
    public static final String NBT_MAX_COOLDOWN = "max_cooldown";

    public static long BASE_WORK_AMOUNT = FluidConstants.BUCKET / 16;

    public int maxCooldown = 40;
    public int cooldown = 0;
    public float prevExtension = 0f;
    public float extension = 0f;
    public boolean extended = false;

    protected IMotorBlockEntity connectedMotor = null;

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

    public static void serverTick(World world, BlockPos pos, BlockState state, LinearOscillatorBlockEntity be)
    {
        be.tick();
    }

    public void tick()
    {
        cooldown = Math.max(0, cooldown - 1);
        maxCooldown = 40;

        if (cooldown <= 0)
        {
            extend();
        }
        else
        {
            extended = false;
        }

        prevExtension = extension;
        extension = cooldown / (float) maxCooldown;
        sync();
    }

    public void extend()
    {
        if (!hasMotor())
            return;

        if (getWorld().getReceivedRedstonePower(getPos()) <= 0)
        {
            getConnectedMotor().setRunning(false);
            return;
        }

        Transaction transaction = Transaction.openOuter();
        long converted = doWork(BASE_WORK_AMOUNT, transaction);
        transaction.commit();

        if (converted != BASE_WORK_AMOUNT)
            return;

        this.cooldown = this.maxCooldown;
        this.extended = true;

        Direction facing = getCachedState().get(BaseFacingBlock.FACING);
        BlockPos facingPos = getPos().offset(facing);

        if (!world.getBlockState(facingPos).isAir() && world.getBlockState(facingPos).getFluidState().isEmpty())
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
        this.maxCooldown = nbt.getInt(NBT_MAX_COOLDOWN);
        this.cooldown = nbt.getInt(NBT_COOLDOWN);
    }

    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt(NBT_MAX_COOLDOWN, maxCooldown);
        nbt.putInt(NBT_COOLDOWN, cooldown);
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        this.prevExtension = nbt.getFloat("prev_extension");
        this.extension= nbt.getFloat("extension");

        if (this.cooldown == this.maxCooldown)
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
    public void setConnectedMotor(@Nullable IMotorBlockEntity motor)
    {
        this.connectedMotor = motor;
    }

    @Override
    public IMotorBlockEntity getConnectedMotor()
    {
        return connectedMotor;
    }
}
