package com.neep.neepmeat.util;

import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.player.implant.PinealEyeImplant;
import com.neep.neepmeat.player.implant.PlayerImplantManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class SightUtil
{
    public static boolean canPlayerSee(PlayerEntity player, Entity entity)
    {
        boolean goggles = player.getInventory().getArmorStack(3).isOf(NMItems.GOGGLES);

        boolean pinealEye = PlayerImplantManager.get(player).getImplant(PinealEyeImplant.ID) != null;

        return goggles || pinealEye;
    }
}
