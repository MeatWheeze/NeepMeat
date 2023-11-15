package com.neep.neepmeat.entity.hound;

import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.util.SightUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class HoundEntity extends HostileEntity implements Monster
{
    public HoundEntity(EntityType<? extends HostileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createLivingAttributes()
    {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.5)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_ARMOR, 2.0);
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity player)
    {
        return super.isInvisibleTo(player) || !SightUtil.canPlayerSee(player, this);
    }

    @Override
    protected void initGoals()
    {
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
//        targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, false, false, livingEntity -> Math.abs(livingEntity.getY() - this.getY()) <= 4.0));

        goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        goalSelector.add(2, new HoundAttackGoal(this, 1.0, false));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source)
    {
        return NMSounds.HOUND_HIT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return NMSounds.HOUND_DEATH;
    }
}
