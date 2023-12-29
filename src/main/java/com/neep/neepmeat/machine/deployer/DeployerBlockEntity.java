package com.neep.neepmeat.machine.deployer;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.util.LazySupplier;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.entity.FakePlayerEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@SuppressWarnings("UnstableApiUsage")
public class DeployerBlockEntity extends SyncableBlockEntity implements SingleSlotStorage<ItemVariant>, MotorisedBlock,
        MotorisedBlock.DiagnosticsProvider
{
    protected final float minPower = 0.02f;
    protected final WritableStackStorage storage;
    public float shuttleOffset;
    protected MotorEntity motor;
    public boolean powered;
    protected LazySupplier<FakePlayerEntity> playerSupplier = LazySupplier.of(() ->
    {
        if (world == null || world.isClient()) return null;

        return new FakePlayerEntity(world.getServer(), (ServerWorld) world, getPos());
    });

    private float power = 0;
    private boolean running;

    public DeployerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        storage = new WritableStackStorage(this::sync);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        fromClientTag(nbt);
        storage.readNbt(nbt);
        powered = nbt.getBoolean("powered");
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        toClientTag(nbt);
        storage.writeNbt(nbt);
        nbt.putBoolean("powered", powered);
    }

    public void deploy(ServerWorld world)
    {
        ServerPlayerEntity fakePlayer1 = this.playerSupplier.get();
        fakePlayer1.setWorld(world);
        fakePlayer1.setPos(pos.getX(), pos.getY(), pos.getZ());
        Direction facing = getCachedState().get(BaseFacingBlock.FACING);
        fakePlayer1.setPitch(0);

        ItemStack stack = storage.getAsStack();
        Item item = stack.getItem();

        BlockPos targetPos = pos.offset(facing);
        fakePlayer1.setStackInHand(Hand.MAIN_HAND, stack);
        Vec3d hitPos = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
        BlockHitResult hit = new BlockHitResult(hitPos, facing.getOpposite(), targetPos, true);

        item.useOnBlock(new ItemUsageContext(fakePlayer1, Hand.MAIN_HAND, hit));

        storage.setStack(stack);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        return storage.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction)
    {
        return storage.extract(resource, maxAmount, transaction);
    }

    @Override
    public boolean isResourceBlank()
    {
        return storage.isResourceBlank();
    }

    @Override
    public ItemVariant getResource()
    {
        return storage.getResource();
    }

    @Override
    public long getAmount()
    {
        return storage.getAmount();
    }

    @Override
    public long getCapacity()
    {
        return storage.getCapacity();
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);
//            System.out.println(getResource() +", " + isResourceBlank() + ", stack: " + stack.isEmpty());
        if ((stack.isEmpty() || !getResource().matches(stack)) && !isResourceBlank())
        {
            Transaction transaction = Transaction.openOuter();
            {
                ItemVariant resource = getResource();
                long extracted = extract(getResource(), Long.MAX_VALUE, transaction);
                player.giveItemStack(resource.toStack((int) extracted));
                transaction.commit();
                sync();
                return true;
            }
        }
        else if (isResourceBlank() && !stack.isEmpty())
        {
            Transaction transaction = Transaction.openOuter();
            {
                long inserted = insert(ItemVariant.of(stack), stack.getCount(), transaction);
                stack.decrement((int) inserted);
                transaction.commit();
                sync();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean tick(MotorEntity motor)
    {
        this.running = power > minPower;

        // I can't be bothered to add a timer
        if (!world.isClient() && world.getTime() % 2 == 0
            && motor.getMechPUPower() >= minPower)
        {
            deploy((ServerWorld) world);
            return true;
        }
        return false;
    }

    @Override
    public void setInputPower(float power)
    {
        this.power = power;
        if (!powered && power > 0) // Rising edge
        {
            powered = true;
            sync();
        }
        else if (powered && power == 0)
        {
            powered = false;
            sync(); // Falling edge
        }
    }

    @Override
    public void markRemoved()
    {
        if (playerSupplier.isInitialised())
        {
            playerSupplier.get().remove(Entity.RemovalReason.DISCARDED);
        }
        super.markRemoved();
    }

    @Override
    public Diagnostics get()
    {
        return Diagnostics.insufficientPower(power < minPower, power, minPower);
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        tryUpdateComparators();
    }
}
