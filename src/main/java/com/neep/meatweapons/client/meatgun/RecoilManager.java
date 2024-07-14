package com.neep.meatweapons.client.meatgun;

import com.neep.meatweapons.network.MeatgunNetwork;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class RecoilManager
{
    private static final Map<UUID, RecoilManager> INSTANCE = new WeakHashMap<>();

    public MeatgunNetwork.RecoilDirection direction;

    public float amount;
    public float horAmount;
    public float returnSpeed;
    public float horReturnSpeed;

    public static RecoilManager getOrCreate(UUID uuid)
    {
        return INSTANCE.computeIfAbsent(uuid, u -> new RecoilManager());
    }

    public void set(MeatgunNetwork.RecoilDirection direction, float amount, float horAmount, float returnSpeed, float horReturnSpeed)
    {
        this.direction = direction;
        this.amount = amount;
        this.horAmount = horAmount;
        this.returnSpeed = returnSpeed;
        this.horReturnSpeed = horReturnSpeed;
    }
}
