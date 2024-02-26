package com.neep.neepmeat.machine.synthesiser;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.entity.EggEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.item.EssentialSaltesItem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("UnstableApiUsage")
public class SynthesiserBlockEntity extends SyncableBlockEntity
{
    public static final float MAX_DISPLACEMENT = -9 / 16f;
    public static final float MIN_DISPLACEMENT = 0;
    public float clientDisplacement;

    protected State state = State.IDLE;
    protected float progress;
    protected int maxProgress;
    protected float increment = 1;
    protected long requiredAmount;
    protected Random random;

    protected SynthesiserStorage storage = new SynthesiserStorage(this);
    private EntityType<?> entityType = null;

    public SynthesiserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.random = new Random(pos.asLong());
    }

    public SynthesiserBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.SYNTHESISER, pos, state);
    }

    protected EntityType<?> getEntityType()
    {
        return entityType;
    }

    public void tick()
    {
        if (state == State.IDLE)
        {
            EntityType<?> type = getEntityType();
            MobSynthesisRegistry.Entry entry;
            if (type != null && (entry = MobSynthesisRegistry.get(type)) != null)
            {
                this.progress = 0;
                this.maxProgress = entry.time();
                this.state = State.RUNNING;
                this.requiredAmount = entry.meat();
                sync();
            }
        }
        else if (state == State.RUNNING)
        {
            this.progress = Math.min(maxProgress, progress + increment);
            if (progress >= maxProgress && createEntity())
            {
                progress = 0;
                state = State.IDLE;
            }
            sync();
        }
    }

    protected boolean createEntity()
    {
        try (Transaction transaction = Transaction.openOuter())
        {
            MobSynthesisRegistry.Entry entry = MobSynthesisRegistry.get(getEntityType());
            if (entry == null) return false;

            long extracted = storage.meatStorage.extract(FluidVariant.of(NMFluids.STILL_MEAT), entry.meat(), transaction);
            Vec3d entityPos;
            if (extracted == entry.meat() && (entityPos = getEntityPos(getEntityType(), pos, world)) != null)
            {
                Entity entity = new EggEntity(world, entityType);
//                Entity entity = getEntityType().create(world);
                entity.setPos(entityPos.x, entityPos.y, entityPos.z);
                entity.setVelocity((random.nextFloat() - 0.5) * 0.1, -0.2, (random.nextFloat() - 0.5) * 0.1);
                world.spawnEntity(entity);
                ((ServerWorld) world).spawnParticles(ParticleTypes.FALLING_SPORE_BLOSSOM, entityPos.x, pos.getY(), entityPos.z, 50, 0.5, 0, 0.5, 0.02);
                transaction.commit();
                return true;
            }
            else transaction.abort();
        }
        return false;
    }

    protected static Vec3d getEntityPos(EntityType<?> entityType, BlockPos pos, World world)
    {
        return Vec3d.ofCenter(pos.down(), 1).subtract(0, entityType.getHeight(), 0);
    }

    public float getClientDisplacement()
    {
        float delta = Math.min(progress / maxProgress, storage.meatStorage.getAmount() / (float) requiredAmount);
        return maxProgress != 0 ? MathHelper.lerp(delta, MAX_DISPLACEMENT, MIN_DISPLACEMENT) : MAX_DISPLACEMENT;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("progress", progress);
        nbt.putInt("maxProgress", maxProgress);
        nbt.putFloat("increment", increment);
        nbt.putInt("state", state.ordinal());
        nbt.putLong("requiredAmount", requiredAmount);
        if (entityType != null) nbt.putString("entityType", Registries.ENTITY_TYPE.getId(entityType).toString());
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.progress = nbt.getFloat("progress");
        this.maxProgress = nbt.getInt("maxProgress");
        this.increment = nbt.getFloat("increment");
        this.state = State.values()[nbt.getInt("state")];
        this.requiredAmount = nbt.getLong("requiredAmount");
        this.entityType = nbt.contains("entityType") ? Registries.ENTITY_TYPE.get(new Identifier(nbt.getString("entityType"))) : null;
        storage.readNbt(nbt);
    }

    public void setEntityType(EntityType<?> type)
    {
        this.entityType = type;
    }

    public boolean changeEntityType(PlayerEntity player, ItemStack heldStack)
    {
        EntityType<?> type = EssentialSaltesItem.getEntityType(heldStack);

        if (getEntityType() != null)
        {
            player.giveItemStack(createItem());
            setEntityType(null);
        }

        if (heldStack.isOf(NMItems.ESSENTIAL_SALTES) && type != null)
        {
            // Spawn an Essential Saltes item corresponding to the previously stored entity type
            setEntityType(type);
            heldStack.decrement(1);
            return entityType != null;
        }
        return false;
    }

    protected ItemStack createItem()
    {
        if (getEntityType() != null)
        {
            ItemStack stack = new ItemStack(NMItems.ESSENTIAL_SALTES);
            EssentialSaltesItem.putEntityType(stack, getEntityType());
            return stack;
        }
        return null;
    }

    protected enum State
    {
        IDLE,
        RUNNING;
    }
}