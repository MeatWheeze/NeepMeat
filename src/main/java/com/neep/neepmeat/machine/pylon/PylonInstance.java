package com.neep.neepmeat.machine.pylon;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.LightType;

@Environment(value = EnvType.CLIENT)
public class PylonInstance extends BlockEntityInstance<PylonBlockEntity> implements DynamicInstance
{
    private final MatrixStack matrices = new MatrixStack();
    private final ModelData rotor;
    private final ModelData activeRotor;

    public PylonInstance(MaterialManager materialManager, PylonBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);

        matrices.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());

        rotor = materialManager.defaultSolid().material(Materials.TRANSFORMED)
                .getModel(NMExtraModels.P_PYLON_ROTOR)
                .createInstance();

        activeRotor = materialManager.defaultSolid().material(Materials.TRANSFORMED)
                .getModel(NMExtraModels.P_PYLON_ACTIVE_ROTOR)
                .createInstance();
    }

    @Override
    public void beginFrame()
    {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        float delta = !MinecraftClient.getInstance().isPaused() ? MinecraftClient.getInstance().getLastFrameDuration() : 0;
        blockEntity.angle = MathHelper.wrapDegrees(blockEntity.angle + delta * blockEntity.getSpeed());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.angle));
        matrices.translate(-0.5, -0.5, -0.5);

        if (blockEntity.isRunning())
        {
            rotor.loadIdentity().scale(0, 0, 0);
            activeRotor.setTransform(matrices);
        }
        else
        {
            activeRotor.loadIdentity().scale(0, 0, 0);
            rotor.setTransform(matrices);
        }
        matrices.pop();
    }

    @Override
    public void updateLight()
    {
        int blight = world.getLightLevel(LightType.BLOCK, getInstancePosition());
        int slight = world.getLightLevel(LightType.SKY, getInstancePosition());
        relight(blight, slight, rotor, activeRotor);
//        relight(getInstancePosition(), rotor);
//        relight(getInstancePosition(), activeRotor);
    }

    @Override
    protected void remove()
    {
        rotor.delete();
        activeRotor.delete();
    }
}
