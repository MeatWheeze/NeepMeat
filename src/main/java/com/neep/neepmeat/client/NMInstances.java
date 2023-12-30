package com.neep.neepmeat.client;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderRegistry;
import com.neep.neepmeat.client.renderer.LinearOscillatorInstance;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.advanced_motor.AdvancedMotorInstance;
import com.neep.neepmeat.machine.grinder.GrinderInstance;
import com.neep.neepmeat.machine.grinder.GrinderRenderer;
import com.neep.neepmeat.machine.motor.MotorInstance;
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
        InstancedRenderRegistry.configure(NMBlockEntities.GRINDER).factory(GrinderInstance::new).apply();
        BlockEntityRendererFactories.register(NMBlockEntities.GRINDER, GrinderRenderer::new);

        InstancedRenderRegistry.configure(NMBlockEntities.ADVANCED_MOTOR).alwaysSkipRender().factory(AdvancedMotorInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.MOTOR).alwaysSkipRender().factory(MotorInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.STIRLING_ENGINE).alwaysSkipRender().factory(StirlingEngineInstance::new).apply();

        InstancedRenderRegistry.configure(NMBlockEntities.PYLON).alwaysSkipRender().factory(PylonInstance::new).apply();
        InstancedRenderRegistry.configure(PLCBlocks.PLC_ENTITY).alwaysSkipRender().factory(PLCInstance::new).apply();
        InstancedRenderRegistry.configure(NMBlockEntities.LINEAR_OSCILLATOR).alwaysSkipRender().factory(LinearOscillatorInstance::new).apply();
    }
}
