package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import com.neep.meatweapons.network.MeatgunNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class HalberdModule extends AbstractMeatgunModule
{
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

        if (player instanceof ServerPlayerEntity serverPlayerEntity)
        {
//            MeatgunNetwork.sendRecoil(serverPlayerEntity, MeatgunNetwork.RecoilDirection.FORWARDS, -20, 0f, 5f, 0.1f);
            if (listener.get().getRoot() instanceof BaseStaffModule staff)
            {
                staff.animateTest(serverPlayerEntity.getWorld().getTime());
            }
        }
    }

    @Override
    public void tick(PlayerEntity player)
    {
        super.tick(player);

    }
}
