package com.neep.meatweapons.entity;

import com.neep.meatweapons.item.GunItem;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class PlayerWeaponManager
{
    protected boolean[] mainStatus = new boolean[2];
    protected boolean[] offStatus = new boolean[2];

    protected final PlayerEntity player;

    public PlayerWeaponManager(PlayerEntity playerEntity)
    {
        this.player = playerEntity;
    }

    public void tick()
    {
        GunItem mainItem = GunItem.getGun(player.getMainHandStack());
        GunItem offItem = GunItem.getGun(player.getOffHandStack());

        double pitch = Math.toRadians(player.getPitch(1));
        double yaw = Math.toRadians(player.getYaw(1));

        ItemStack stack;
        if (mainItem != null)
        {
            stack = player.getMainHandStack();
            if (mainStatus[0]) mainItem.tickTrigger(player.getWorld(), player, stack, MWAttackC2SPacket.TRIGGER_PRIMARY, pitch, yaw, MWAttackC2SPacket.HandType.MAIN);
            if (mainStatus[1]) mainItem.tickTrigger(player.getWorld(), player, stack, MWAttackC2SPacket.TRIGGER_SECONDARY, pitch, yaw, MWAttackC2SPacket.HandType.MAIN);
        }

        if (offItem != null)
        {
            stack = player.getOffHandStack();
            if (offStatus[0]) offItem.tickTrigger(player.getWorld(), player, stack, MWAttackC2SPacket.TRIGGER_PRIMARY, pitch, yaw, MWAttackC2SPacket.HandType.OFF);
            if (offStatus[1]) offItem.tickTrigger(player.getWorld(), player, stack, MWAttackC2SPacket.TRIGGER_SECONDARY, pitch, yaw, MWAttackC2SPacket.HandType.OFF);
        }
    }

    public void updateStatus(MWAttackC2SPacket.HandType handType, int triggerId, MWAttackC2SPacket.ActionType actionType)
    {

        if (handType.mainHand())
        {
            mainStatus[0] = triggerId == MWAttackC2SPacket.TRIGGER_PRIMARY ? actionType.pressed() : mainStatus[0];
            mainStatus[1] = triggerId == MWAttackC2SPacket.TRIGGER_SECONDARY ? actionType.pressed() : mainStatus[1];
        }

        if (handType.offHand())
        {
            offStatus[0] = triggerId == MWAttackC2SPacket.TRIGGER_PRIMARY ? actionType.pressed() : offStatus[0];
            offStatus[1] = triggerId == MWAttackC2SPacket.TRIGGER_SECONDARY ? actionType.pressed() : offStatus[1];
        }
    }
}
