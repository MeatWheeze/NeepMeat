package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class HalberdModule extends AbstractMeatgunModule
{
    private boolean triggerHeld;

    public HalberdModule(MeatgunComponent.Listener listener)
    {
        super(listener);
    }

    public HalberdModule(MeatgunComponent.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.HALBERD;
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        super.trigger(world, player, stack, id, pitch, yaw, handType);

        if (id == 2)
        {
            MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("blade_swing_down"); // Sword attack
        }

        if (id == 1)
        {
            triggerHeld = true;
            listener.markDirty();
            MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("upper_thrust");
        }
    }

    @Override
    public void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        super.release(world, player, stack, id, pitch, yaw, handType);

        if (id == 1)
        {
            triggerHeld = false;
            listener.markDirty();
        }
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        super.tickTrigger(world, player, stack, id, pitch, yaw, handType);

    }

    @Override
    public void tick(PlayerEntity player)
    {
        super.tick(player);

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("trigger_held", triggerHeld);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.triggerHeld = nbt.getBoolean("trigger_held");
    }

    public boolean triggerHeld()
    {
        return triggerHeld;
    }
}
