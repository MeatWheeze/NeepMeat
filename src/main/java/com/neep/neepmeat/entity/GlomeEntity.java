package com.neep.neepmeat.entity;

import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.init.NMParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

import java.util.Collections;

public class GlomeEntity extends FlyingEntity
{
    protected int time;

    public GlomeEntity(EntityType<? extends FlyingEntity> entityType, World world)
    {
        super(entityType, world);
        this.setNoGravity(true);
        this.setCustomNameVisible(false);
        boolean bl = this.isCustomNameVisible();
    }

    public GlomeEntity(World world, double x, double y, double z, double vx, double vy, double vz)
    {
        this(NMEntities.GLOME, world);
        this.setPos(x, y, z);
        this.setVelocity(vx, vy, vz);
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return DefaultAttributeContainer.builder().add(EntityAttributes.GENERIC_MAX_HEALTH).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).add(EntityAttributes.GENERIC_MOVEMENT_SPEED).add(EntityAttributes.GENERIC_ARMOR).add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).add(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    @Override
    public Iterable<ItemStack> getArmorItems()
    {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack)
    {

    }

    @Override
    public Packet<?> createSpawnPacket()
    {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet)
    {
        super.onSpawnPacket(packet);
        spawnParticles(15, 0.1f);
    }

    @Override
    public void tick()
    {
        super.tick();
        ++time;

        if (this.time >= 160) this.remove(RemovalReason.DISCARDED);
    }

    @Override
    public Arm getMainArm()
    {
        return Arm.LEFT;
    }

    public int getLifeTime()
    {
        return time;
    }

    private void spawnParticles(int amount, float d)
    {
        for (int i = 0; i < amount; ++i)
        {
            world.addParticle(NMParticles.MEAT_SPLASH, getX(), getY(), getZ(),
                    d * (random.nextFloat() - 0.5),
                    d * (random.nextFloat() - 0.5),
                    d * (random.nextFloat() - 0.5));
        }
    }
}