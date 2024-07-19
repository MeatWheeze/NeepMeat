package com.neep.meatweapons.client.meatgun;

import com.neep.meatweapons.network.MeatgunNetwork;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;

import java.util.Map;
import java.util.UUID;

public class RecoilManager
{
    private static final Map<UUID, RecoilManager> INSTANCES = new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.SOFT, true);

    public MeatgunNetwork.RecoilDirection direction;

    public float amount;
    public float horAmount;
    public float returnSpeed;
    public float horReturnSpeed;

    public static RecoilManager getOrCreate(UUID uuid)
    {
        return INSTANCES.computeIfAbsent(uuid, u -> new RecoilManager());
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
