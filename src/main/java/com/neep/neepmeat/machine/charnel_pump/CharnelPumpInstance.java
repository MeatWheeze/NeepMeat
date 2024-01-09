package com.neep.neepmeat.machine.charnel_pump;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class CharnelPumpInstance extends BlockEntityInstance<CharnelPumpBlockEntity> implements DynamicInstance
{
    private final MatrixStack matrices = new MatrixStack();
    private final ModelData plunger;

    public CharnelPumpInstance(MaterialManager materialManager, CharnelPumpBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        plunger = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.CHARNEL_PUMP_PLUNGER).createInstance();
        matrices.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());
    }

    @Override
    protected void remove()
    {
        plunger.delete();
    }

    @Override
    public void beginFrame()
    {
        matrices.push();

        double s = Math.sin((blockEntity.getWorld().getTime() + MinecraftClient.getInstance().getTickDelta()) / 100f);

        matrices.translate(0, 8 * s, 0);

        plunger.setTransform(matrices);
        matrices.pop();
    }
}
