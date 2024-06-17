package com.neep.neepmeat.client;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.neep.neepmeat.client.instance.*;
import com.neep.neepmeat.client.renderer.LinearOscillatorInstance;
import com.neep.neepmeat.client.renderer.block.LargeTrommelRenderer;
import com.neep.neepmeat.client.renderer.block.LargestHopperRenderer;
import com.neep.neepmeat.client.renderer.entity.LimbEntityInstance;
import com.neep.neepmeat.entity.follower.FollowerInstance;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.machine.advanced_motor.AdvancedMotorInstance;
import com.neep.neepmeat.machine.charnel_pump.CharnelPumpInstance;
import com.neep.neepmeat.machine.fabricator.FabricatorInstance;
import com.neep.neepmeat.machine.flywheel.FlywheelInstance;
import com.neep.neepmeat.machine.grinder.GrinderInstance;
import com.neep.neepmeat.machine.grinder.GrinderRenderer;
import com.neep.neepmeat.machine.hydraulic_press.HydraulicPressInstance;
import com.neep.neepmeat.machine.large_crusher.CrusherSegmentInstance;
import com.neep.neepmeat.machine.large_crusher.LargeCrusherInstance;
import com.neep.neepmeat.machine.large_crusher.LargeCrusherRenderer;
import com.neep.neepmeat.machine.large_motor.LargeMotorInstance;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
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

        InstancedRenderRegistry.configure(LivingMachines.CRUSHER_SEGMENT_BE).factory(CrusherSegmentInstance::new).apply();

        InstancedRenderRegistry.configure(LivingMachines.LARGE_TROMMEL_BE).factory(LargeTrommelInstance::new).apply();
        BlockEntityRendererFactories.register(LivingMachines.LARGE_TROMMEL_BE, LargeTrommelRenderer::new);

        BlockEntityRendererFactories.register(LivingMachines.LARGEST_HOPPER_BE, LargestHopperRenderer::new);

        InstancedRenderRegistry.configure(LivingMachines.LUCKY_ONE_BE).alwaysSkipRender().factory(LuckyOneInstance::new).apply();

        InstancedRenderRegistry.configure(NMBlockEntities.MOTOR).alwaysSkipRender().factory(MotorInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.ADVANCED_MOTOR).alwaysSkipRender().factory(AdvancedMotorInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.LARGE_MOTOR).alwaysSkipRender().factory(LargeMotorInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.STIRLING_ENGINE).alwaysSkipRender().factory(StirlingEngineInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.FLYWHEEL).alwaysSkipRender().factory(FlywheelInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.HYDRAULIC_PRESS).alwaysSkipRender().factory(HydraulicPressInstance::new).apply();
        InstancedRenderRegistry.configure(LivingMachines.CHARNEL_PUMP_BE).factory(CharnelPumpInstance::new).apply();

        InstancedRenderRegistry.configure(NMBlockEntities.FABRICATOR).alwaysSkipRender().factory(FabricatorInstance::new).apply();

        InstancedRenderRegistry.configure(PLCBlocks.ROBOTIC_ARM_ENTITY).alwaysSkipRender().factory(RoboticArmInstance::new).apply();

        InstancedRenderRegistry.configure(NMBlockEntities.PYLON).alwaysSkipRender().factory(PylonInstance::new).apply();
        InstancedRenderRegistry.configure(PLCBlocks.PLC_ENTITY).alwaysSkipRender().factory(PLCInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.LINEAR_OSCILLATOR).alwaysSkipRender().factory(LinearOscillatorInstance::new).apply();

        InstancedRenderRegistry.configure(NMBlockEntities.LARGE_FAN).alwaysSkipRender().factory(LargeFanInstance::new).apply();

        InstancedRenderRegistry.configure(NMEntities.PHAGE_RAY).alwaysSkipRender().factory(PhageRayInstance::new).apply();
        InstancedRenderRegistry.configure(LivingMachines.TREE_VACUUM_BE).alwaysSkipRender().factory(TreeVacuumInstance::new).apply();

        InstancedRenderRegistry.configure(NMEntities.FOLLOWER).factory(FollowerInstance::new).apply();

        InstancedRenderRegistry.configure(NMEntities.LIMB).factory(LimbEntityInstance::new).apply();

        InstancedRenderRegistry.configure(NMEntities.FARMING_SCUTTER).factory(FarmingScutterInstance::new).apply();
    }
}
