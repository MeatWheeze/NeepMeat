package com.neep.neepmeat.entity;

import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.item.EssentialSaltesItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class EggEntity extends SimpleEntity
{
    private static final TrackedData<Integer> WOBBLE_TICKS = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> WOBBLE_STRENGTH = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> WOBBLE_DIRECTION = DataTracker.registerData(EggEntity.class, TrackedDataHandlerRegistry.FLOAT);

    protected EntityType<?> hatchType;
    protected int growthTicks;

    public EggEntity(EntityType<? extends Entity> entityType, World world)
    {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        this.dataTracker.startTracking(WOBBLE_TICKS, 0);
        this.dataTracker.startTracking(WOBBLE_STRENGTH, 0f);
        this.dataTracker.startTracking(WOBBLE_DIRECTION, 1f);
    }

    public EggEntity(World world, EntityType<?> hatchType)
    {
        super(NMEntities.EGG, world);
        this.hatchType = hatchType;
    }

    @Override
    public void tick()
    {
        super.tick();

        this.setWobbleTicks(Math.max(0, this.getWobbleTicks() - 1));
        this.setWobbleStrength(Math.max(0, this.getWobbleStrength() - 1));
        tickMovement();
        if (random.nextFloat() < 0.005)
        {
            setWobbleTicks(10);
            setWobbleStrength(10);
            invertWobbleDirection();
        }

        if (canGrow())
        {
            ++growthTicks;
        }

        if (growthTicks > 100)
        {
            hatch();
        }
    }

    public boolean shouldSwim()
    {
        return true;
    }

    private void hatch()
    {
        if (!getWorld().isClient() && getHatchType() != null)
        {
            Entity entity = getHatchType().create(getWorld());
            if (entity != null)
            {
                entity.setPosition(getPos());
                getWorld().spawnEntity(entity);
            }
            this.remove(RemovalReason.DISCARDED);
            ((ServerWorld) getWorld()).spawnParticles(NMParticles.MEAT_SPLASH, entity.getX(), entity.getY(), entity.getZ(), 30, 1, 2, 1, 0.01);
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount)
    {
        setWobbleTicks(10);
        setWobbleStrength(getWobbleStrength() + amount * 10.0f);
        invertWobbleDirection();
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
        if (!getWorld().isClient() && getWobbleStrength() > 30.0f)
        {
            this.onDeath();
        }
        return super.damage(source, amount);
    }

    public void onDeath()
    {
        if (this.isRemoved())
        {
            return;
        }
        ItemStack stack = new ItemStack(NMItems.MOB_EGG);
        EssentialSaltesItem.putEntityType(stack, hatchType);
        dropStack(stack);
        this.kill();
        this.getWorld().sendEntityStatus(this, (byte)3);
    }

    public boolean canGrow()
    {
        return getWorld().getFluidState(getBlockPos()).isOf(NMFluids.STILL_BLOOD);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        if (hatchType != null) nbt.putString("hatchType", Registries.ENTITY_TYPE.getId(hatchType).toString());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.hatchType = nbt.contains("hatchType") ? Registries.ENTITY_TYPE.get(new Identifier(nbt.getString("hatchType"))) : null;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {

    }

    public void setWobbleTicks(int wobbleTicks)
    {
        this.dataTracker.set(WOBBLE_TICKS, wobbleTicks);
    }

    public int getWobbleTicks()
    {
        return this.dataTracker.get(WOBBLE_TICKS);
    }

    public void setWobbleStrength(float strength)
    {
        this.dataTracker.set(WOBBLE_STRENGTH, strength);
    }

    public float getWobbleStrength()
    {
        return this.dataTracker.get(WOBBLE_STRENGTH);
    }

    public void invertWobbleDirection()
    {
        this.dataTracker.set(WOBBLE_DIRECTION, -dataTracker.get(WOBBLE_DIRECTION));
    }

    public EntityType<?> getHatchType()
    {
        return hatchType;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    public float getWobbleDirection()
    {
        return dataTracker.get(WOBBLE_DIRECTION);
    }
}
