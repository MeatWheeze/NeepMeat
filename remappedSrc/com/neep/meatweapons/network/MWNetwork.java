package com.neep.meatweapons.network;

import com.neep.meatweapons.MeatWeapons;
import net.minecraft.util.Identifier;

public class MWNetwork
{
    public static final Identifier SPAWN_ID = new Identifier(MeatWeapons.NAMESPACE, "spawn_packet");
    public static final Identifier EFFECT_ID = new Identifier(MeatWeapons.NAMESPACE, "effect");
}
