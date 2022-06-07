package com.neep.neepmeat.blockentity;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.entity.FakePlayerEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
public class DeployerBlockEntity extends BlockEntity implements SingleSlotStorage<ItemVariant>, BlockEntityClientSerializable
{
    protected final WritableStackStorage storage;

    public float shuttleOffset;
    public int shuttleTicks;

    protected int cooldown;
    public boolean shuttle;
    public long shuttleTime;

    public static final int COOLDOWN = 10;

    public DeployerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        storage = new WritableStackStorage(this);
    }

    public DeployerBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.DEPLOYER, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        return nbt;
    }

    public void update(BlockPos fromPos)
    {
        if (getWorld().getReceivedRedstonePower(getPos()) > 0)
        {
//            if (shuttleTime - world.getTime() + COOLDOWN < 0)
                deploy((ServerWorld) world);
        }
    }

    public void deploy(ServerWorld world)
    {
        syncShuttle();

        ServerPlayerEntity fakePlayer = new FakePlayerEntity(world.getServer(), world, pos);
        fakePlayer.setWorld(world);
        fakePlayer.setPos(pos.getX(), pos.getY(), pos.getZ());
        Direction facing = getCachedState().get(BaseFacingBlock.FACING);
        fakePlayer.setPitch(0);

        ItemStack stack = storage.getAsStack();
        Item item = stack.getItem();

        BlockPos targetPos = pos.offset(facing);
        fakePlayer.setStackInHand(Hand.MAIN_HAND, stack);
        Vec3d hitPos = new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5);
        BlockHitResult hit = new BlockHitResult(hitPos, facing.getOpposite(), targetPos, true);

        item.useOnBlock(new ItemUsageContext(fakePlayer, Hand.MAIN_HAND, hit));

        storage.setStack(stack);

        fakePlayer.remove(Entity.RemovalReason.DISCARDED);

//        Box box = new Box(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 2, pos.getY() + 2, pos.getZ() + 2);
//        System.out.println(world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), box, entity -> true));
    }

    public void syncShuttle()
    {
        this.shuttleTime = world.getTime();
        this.shuttle = true;
        sync();
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
    public void fromClientTag(NbtCompound tag)
    {
//       System.out.println("reading client");
        storage.readNbt(tag);

        if (tag.getBoolean("shuttle"))
        {
            this.shuttleTime = world.getTime();
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
//        System.out.println("writing server");
        storage.writeNbt(tag);
        tag.putBoolean("shuttle", shuttle);
        this.shuttle = false;
        return tag;
    }
}
