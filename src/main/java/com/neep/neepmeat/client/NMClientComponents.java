package com.neep.neepmeat.client;

import com.neep.meatlib.util.ClientComponents;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.advanced_integrator.AdvancedIntegratorClientComponent;
import com.neep.neepmeat.machine.charnel_pump.CharnelPumpClient;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import com.neep.neepmeat.machine.pylon.PylonClientComponent;

public class NMClientComponents
{
    public static void init()
    {
        ClientComponents.register(LivingMachines.CHARNEL_PUMP_BE, CharnelPumpClient::new);
        ClientComponents.register(NMBlockEntities.ADVANCED_INTEGRATOR, AdvancedIntegratorClientComponent::new);
        ClientComponents.register(NMBlockEntities.PYLON, PylonClientComponent::new);
    }
}
