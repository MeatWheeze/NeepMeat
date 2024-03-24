package com.neep.neepmeat.client;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.neep.neepmeat.client.instance.RoboticArmInstance;
import com.neep.neepmeat.client.renderer.LinearOscillatorInstance;
import com.neep.neepmeat.client.renderer.entity.LimbEntityInstance;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.machine.advanced_motor.AdvancedMotorInstance;
import com.neep.neepmeat.machine.charnel_pump.CharnelPumpInstance;
import com.neep.neepmeat.machine.flywheel.FlywheelInstance;
import com.neep.neepmeat.machine.grinder.GrinderInstance;
import com.neep.neepmeat.machine.grinder.GrinderRenderer;
import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressInstance;
import com.neep.neepmeat.machine.large_crusher.LargeCrusherInstance;
import com.neep.neepmeat.machine.large_crusher.LargeCrusherRenderer;
import com.neep.neepmeat.machine.large_motor.LargeMotorInstance;
import com.neep.neepmeat.machine.motor.MotorInstance;
import com.neep.neepmeat.machine.phage_ray.PhageRayInstance;
import com.neep.neepmeat.machine.pylon.PylonInstance;
import com.neep.neepmeat.machine.stirling_engine.StirlingEngineInstance;
import com.neep.neepmeat.machine.surgical_controller.PLCInstance;
import com.neep.neepmeat.plc.PLCBlocks;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class NMInstances
{
    public static void init()
    {
        // Flywheel
        InstancedRenderRegistry.configure(NMBlockEntities.CRUSHER).factory(GrinderInstance::new).apply();
        BlockEntityRendererFactories.register(NMBlockEntities.CRUSHER, GrinderRenderer::new);

        InstancedRenderRegistry.configure(NMBlockEntities.LARGE_CRUSHER).factory(LargeCrusherInstance::new).apply();
        BlockEntityRendererFactories.register(NMBlockEntities.LARGE_CRUSHER, LargeCrusherRenderer::new);

        InstancedRenderRegistry.configure(NMBlockEntities.MOTOR).alwaysSkipRender().factory(MotorInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.ADVANCED_MOTOR).alwaysSkipRender().factory(AdvancedMotorInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.LARGE_MOTOR).alwaysSkipRender().factory(LargeMotorInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.STIRLING_ENGINE).alwaysSkipRender().factory(StirlingEngineInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.FLYWHEEL).alwaysSkipRender().factory(FlywheelInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.HYDRAULIC_PRESS).alwaysSkipRender().factory(HydraulicPressInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.CHARNEL_PUMP).alwaysSkipRender().factory(CharnelPumpInstance::new).apply();

        InstancedRenderRegistry.configure(PLCBlocks.ROBOTIC_ARM_ENTITY).alwaysSkipRender().factory(RoboticArmInstance::new).apply();

        InstancedRenderRegistry.configure(NMBlockEntities.PYLON).alwaysSkipRender().factory(PylonInstance::new).apply();
        InstancedRenderRegistry.configure(PLCBlocks.PLC_ENTITY).alwaysSkipRender().factory(PLCInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.LINEAR_OSCILLATOR).alwaysSkipRender().factory(LinearOscillatorInstance::new).apply();

        InstancedRenderRegistry.configure(NMEntities.PHAGE_RAY).alwaysSkipRender().factory(PhageRayInstance::new).apply();

        InstancedRenderRegistry.configure(NMEntities.LIMB).factory(LimbEntityInstance::new).apply();
    }
}
