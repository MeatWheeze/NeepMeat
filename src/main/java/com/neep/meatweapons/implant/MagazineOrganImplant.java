package com.neep.meatweapons.implant;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.item.AmmunitionItem;
import com.neep.meatweapons.meatgun.AmmunitionProvider;
import com.neep.meatweapons.meatgun.AmmunitionType;
import com.neep.neepmeat.implant.player.EntityImplant;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MagazineOrganImplant implements EntityImplant
{
    public static final Identifier ID = new Identifier(MeatWeapons.NAMESPACE, "magazine_organ");

    private final Entity entity;
    private int maxSize = 6;

//    private final AmmunitionProvider[] inventory = new AmmunitionProvider[6];
    private final List<AmmunitionProvider> inventory = new ArrayList<>();

    public MagazineOrganImplant(Entity entity)
    {
        this.entity = entity;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }

    @Override
    public void tick()
    {

    }

    public boolean canConsume(AmmunitionItem ammunitionItem, ItemStack itemStack)
    {
        return inventory.size() < maxSize;
    }

    private void insert(AmmunitionProvider provider)
    {
        if (inventory.size() < maxSize)
        {
            inventory.add(provider);
        }
    }

    public ItemStack consume(World world, ItemStack itemStack, AmmunitionProvider provider)
    {
        world.playSound(
                null,
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                SoundEvents.ENTITY_GENERIC_EAT,
                SoundCategory.PLAYERS,
                1.0F,
                1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F
        );

//        if (!(entity instanceof PlayerEntity player) || !player.getAbilities().creativeMode)
//        {
//            itemStack.decrement(1);
//        }

        entity.emitGameEvent(GameEvent.EAT);

        insert(provider);
        provider.consume();

        return itemStack;
    }

    public AmmunitionProvider provide(AmmunitionType type, int amount)
    {
        inventory.sort(Comparator.comparingInt(AmmunitionProvider::getAmount));
        var it = inventory.iterator();
        while (it.hasNext())
        {
            AmmunitionProvider provider = it.next();
            if (provider.ammoType() == type && provider.getAmount() >= amount)
            {
                it.remove();
                return provider;
            }
        }
        return null;
    }
}
