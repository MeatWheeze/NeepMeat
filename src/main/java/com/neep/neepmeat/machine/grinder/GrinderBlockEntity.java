package com.neep.neepmeat.machine.grinder;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import com.neep.neepmeat.recipe.GrindingRecipe;
import com.neep.neepmeat.transport.api.pipe.IItemPipe;
import com.neep.neepmeat.util.ItemInPipe;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class GrinderBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    protected GrinderStorage storage = new GrinderStorage(this);
    protected int cooldownTicks = 2;
    protected int processLength;

    public static final float INCREMENT_MAX = 2;
    public static final float INCREMENT_MIN = 0.2f;
    public static final float MULTIPLIER_MIN = 0.05f;
    protected float progressIncrement;
    protected float progress;

    protected Identifier currentRecipeId;
    protected GrindingRecipe currentRecipe;


    public GrinderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public GrinderBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.GRINDER, pos, state);
    }

    public GrindingRecipe getCurrentRecipe()
    {
        return currentRecipe;
    }

    public void setCurrentRecipe(@Nullable GrindingRecipe recipe)
    {
        this.currentRecipe = recipe;
        this.currentRecipeId = recipe != null ? recipe.getId() : null;
    }

    public void readCurrentRecipe()
    {
        if (world != null)
        {
            Optional<? extends Recipe<?>> optional = getWorld().getRecipeManager().get(currentRecipeId);
            optional.ifPresentOrElse(recipe -> this.currentRecipe = (GrindingRecipe) recipe,
                    () -> this.currentRecipe = null);
        }
    }

    public GrinderStorage getStorage()
    {
        return storage;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putFloat("progress", progress);
        nbt.putInt("process_length", processLength);

        if (currentRecipe != null)
            nbt.putString("current_recipe", currentRecipe.getId().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
        this.progress = nbt.getFloat("progress");
        this.processLength = nbt.getInt("process_length");
        this.currentRecipeId = new Identifier(nbt.getString("current_recipe"));
        readCurrentRecipe();
    }

    public void tick()
    {
        readCurrentRecipe();

        if (progressIncrement == 0)
        {
            currentRecipe = null;
            return;
        }

        // Eject outputs
        if (!storage.getOutputStorage().isEmpty())
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                ejectOutput(transaction);
                transaction.commit();
            }
        }

        if (currentRecipe != null)
        {
            progress = Math.min(processLength, progress + progressIncrement);

            ((ServerWorld) world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, getCurrentRecipe().getItemOutput().resource().getDefaultStack()),
                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 1, 0.1, 0, 0.1, 0.01);

            if (progress >= this.processLength || !getCurrentRecipe().matches(storage, world))
            {
                endDutyCycle();
                this.progress = 0;
            }
        }
        else
        {
            ++progress;

            if (progress >= this.cooldownTicks)
            {
                startDutyCycle();
                this.progress = 0;
            }
        }
    }

    private void startDutyCycle()
    {
        if (currentRecipe == null && storage.outputStorage.isEmpty() && !storage.inputStorage.isEmpty())
        {
            GrindingRecipe recipe = getWorld().getRecipeManager().getFirstMatch(NMrecipeTypes.GRINDING, storage, world).orElse(null);

            if (recipe != null && storage.outputStorage.simulateInsert(ItemVariant.of(recipe.getItemOutput().resource()),
                    recipe.getItemOutput().amount(), null) == recipe.getItemOutput().amount())
            {
//                try (Transaction transaction = Transaction.openOuter())
//                {
//                    if (recipe.takeInputs(storage, transaction))
//                    {
//                        transaction.commit();
                setCurrentRecipe(recipe);
                this.processLength = recipe.getTime();
//                    }
//                    else
//                        transaction.abort();
//                }
            }
        }
        sync();
    }

    private void endDutyCycle()
    {
        if (currentRecipe != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                if (getCurrentRecipe().matches(storage, world) && getCurrentRecipe().takeInputs(storage, transaction) && getCurrentRecipe().ejectOutput(storage, transaction))
                {
                    ejectOutput(transaction);
                    transaction.commit();
                }
                else transaction.abort();
            }
            this.setCurrentRecipe(null);
        }
        sync();
    }

    protected void ejectOutput(TransactionContext transaction)
    {
        Direction facing = getCachedState().get(GrinderBlock.FACING);
        CombinedStorage<ItemVariant, WritableStackStorage> combined = new CombinedStorage<>(List.of(storage.outputStorage, storage.extraStorage));
        storageToWorld(getWorld(), combined, pos.offset(facing), facing.getOpposite(), transaction);

        Vec3d xpPos = Vec3d.ofCenter(pos, 0.5).add(facing.getOffsetX() * 0.6, facing.getOffsetY() * 0.6, facing.getOffsetZ() * 0.6);
        ExperienceOrbEntity.spawn((ServerWorld) world, xpPos, (int) Math.ceil(storage.getXpStorage().getAmount()));
        storage.xpStorage.extract(Float.MAX_VALUE, transaction);
    }

//    @Override
//    public void update(ServerWorld world, BlockPos pos, BlockPos fromPos, BlockState state)
//    {
//        Direction facing = state.get(GrinderBlock.FACING);
//        for (Direction direction : Direction.values())
//        {
//            if (direction == facing || direction == Direction.UP || direction == Direction.DOWN)
//                continue;
//
//            BlockPos offset = pos.offset(direction);
//            if (world.getBlockEntity(offset) instanceof IMotorBlockEntity be
//                    && world.getBlockState(offset).get(BaseFacingBlock.FACING) == direction.getOpposite())
//            {
//                setConnectedMotor(be);
//                return;
//            }
//        }
//        setConnectedMotor(null);
//    }

    public static void storageToWorld(World world, Storage<ItemVariant> storage, BlockPos toPos, Direction direction, TransactionContext transaction)
    {
        BlockState state = world.getBlockState(toPos);
        Storage<ItemVariant> ejectStorage = ItemStorage.SIDED.find(world, toPos, direction);
        for (StorageView<ItemVariant> view : storage.iterable(transaction))
        {
            try (Transaction inner = transaction.openNested())
            {
                if (view.isResourceBlank())
                {
                    inner.abort();
                    continue;
                }

                long maxAmount = view.getAmount();
                long transferred = 0;
                ItemStack stack = view.getResource().toStack((int) maxAmount);
                if (ejectStorage != null)
                {
                    transferred = ejectStorage.insert(view.getResource(), maxAmount, inner);
                }
                else if (state.getBlock() instanceof IItemPipe pipe && pipe.getConnections(state, d -> true).contains(direction))
                {
//                    transferred = pipe.insert(world, toPos, state, direction, new ItemInPipe(stack, world.getTime())) == -1 ? maxAmount : 0;
                    transferred = pipe.insert(world, toPos, state, direction, new ItemInPipe(stack, world.getTime()), transaction);
                }
                else
                {
                    Direction facing = direction.getOpposite();
                    Vec3d itemPos = Vec3d.ofCenter(toPos, 0.5);
                    ItemEntity entity = new ItemEntity(world, itemPos.x, itemPos.y, itemPos.z, stack);
                    float mult = 0.1f;
                    entity.setVelocity(facing.getOffsetX() * mult, facing.getOffsetY() * mult, facing.getOffsetZ() * mult);
                    world.spawnEntity(entity);
                    transferred = maxAmount;
                }
                long extracted = view.extract(view.getResource(), maxAmount, inner);
                if (transferred == maxAmount && extracted == maxAmount)
                {
                    inner.commit();
                    continue;
                }
                inner.abort();
            }
        }
    }

    @Override
    public boolean tick(IMotorBlockEntity motor)
    {
        tick();
        return currentRecipe != null;
    }

    @Override
    public void setWorkMultiplier(float multiplier)
    {
        this.progressIncrement = MathHelper.lerp(multiplier, 0, INCREMENT_MAX);
        if (multiplier < MULTIPLIER_MIN) progressIncrement = 0;
    }
}
