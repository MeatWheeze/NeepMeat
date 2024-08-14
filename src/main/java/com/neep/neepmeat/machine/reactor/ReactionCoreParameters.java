package com.neep.neepmeat.machine.reactor;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.machine.reactor.block.entity.CoreSensorBlockEntity;
import net.minecraft.util.math.MathHelper;

public class ReactionCoreParameters
{
    private boolean dead;

    private double organisation = 0;
    private double organisationFlow = 0;
    private double incidentZoneRadius = 0;
    private double exudateMassFlow = 0;
    private double storedExudate = 0;
    private double cudEfficiency = 0;

    public void tick(float disruption, float maxOrganisation)
    {
        float km = 0.04f; // Organisation to mass flow
        float ke = 1;

        float maxOrganisationIncrease = 0.1f;
        organisationFlow = Math.min(maxOrganisationIncrease, maxOrganisation - organisation);

        organisation = MathHelper.clamp(organisation - disruption + organisationFlow, 0, maxOrganisation);

        exudateMassFlow = km * organisation;
        storedExudate += exudateMassFlow;

        if (maxOrganisation == 0)
            cudEfficiency = 0;
        else
            cudEfficiency = ke * MathHelper.clamp(organisationFlow, 0, maxOrganisation) / maxOrganisation;

        NeepMeat.LOGGER.info("Organisation {}, Cud efficiency {}, Exudate mass flow {}, Stored exudate {}, Incident zone radius {}",
                organisation, cudEfficiency, exudateMassFlow, storedExudate, incidentZoneRadius);

        if (organisation == 0)
        {
            dead = true;
        }
    }

    public void tickIncidentZone()
    {
        float k1 = 0.03f;
        float k2 = 0.1f;
        incidentZoneRadius = k1 * storedExudate + k2 * exudateMassFlow;
    }

    public void emitSensorData(CoreSensorBlockEntity sensor)
    {
        sensor.organisation.send(organisation);
        sensor.incidentZoneRadius.send(incidentZoneRadius);
        sensor.exudateFlow.send(exudateMassFlow * 10);
        sensor.storedExudate.send(storedExudate * 10);
        sensor.cudEfficiency.send(cudEfficiency * 100);
    }

    public double getIncidentZoneRadius()
    {
        return incidentZoneRadius;
    }

    public double getCudEfficiency()
    {
        return cudEfficiency;
    }

    public double getStoredExudate()
    {
        return storedExudate;
    }

    public void extractStored(double toExtract)
    {
        storedExudate -= toExtract;
    }

    public boolean isDead()
    {
        return dead;
    }
}
