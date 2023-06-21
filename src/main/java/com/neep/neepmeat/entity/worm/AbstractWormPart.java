package com.neep.neepmeat.entity.worm;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.world.World;

import java.util.Collections;

public abstract class AbstractWormPart extends LivingEntity implements Monster
{
    protected AbstractWormPart(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
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
    public Arm getMainArm()
    {
        return Arm.LEFT;
    }
}
