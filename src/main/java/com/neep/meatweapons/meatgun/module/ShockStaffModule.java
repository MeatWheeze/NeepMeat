package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
import com.neep.neepmeat.init.NMSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

import java.util.List;

public class ShockStaffModule extends MeleeModule
{
    private int swingDownCooldown;

    public ShockStaffModule(MeatgunComponent.Listener listener)
    {
        super(listener, List.of());
    }

    public ShockStaffModule(MeatgunComponent.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.SHOCK_STAFF;
    }

    @Override
    public void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        if (id == 2 && swingDownCooldown == 0)
        {
            MeatgunNetwork.SEND_ANIMATION.emitter(player).apply("swing_across", null);
            world.playSoundFromEntity(null, player, NMSounds.SHOCK_STAFF_ATTACK, SoundCategory.PLAYERS, 1, 1);
            MeatgunNetwork.sendRecoil((ServerPlayerEntity) player, MeatgunNetwork.RecoilDirection.UP, 7, 0.3f,0.7f, 0.02f);
            fireBeam(world, player, pitch, yaw, 3);
            swingDownCooldown = 15;
        }
    }

    @Override
    public void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
    }

    @Override
    public void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        super.tickTrigger(world, player, stack, id, pitch, yaw, handType);
    }

    @Override
    public void tick(PlayerEntity player)
    {
        swingDownCooldown = Math.max(0, swingDownCooldown - 1);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("down_cooldown", swingDownCooldown);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.swingDownCooldown = nbt.getInt("down_cooldown");
    }
}
