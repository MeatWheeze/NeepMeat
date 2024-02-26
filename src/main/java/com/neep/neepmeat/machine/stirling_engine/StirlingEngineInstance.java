package com.neep.neepmeat.machine.stirling_engine;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(value = EnvType.CLIENT)
public class StirlingEngineInstance extends BlockEntityInstance<StirlingEngineBlockEntity> implements DynamicInstance
{
    private final MatrixStack matrices = new MatrixStack();
    private final ModelData rotor;

    public StirlingEngineInstance(MaterialManager materialManager, StirlingEngineBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        matrices.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());

        rotor = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.STIRLING_ENGINE_ROTOR).createInstance();
    }

    @Override
    protected void remove()
    {
        rotor.delete();
    }

    @Override
    public void beginFrame()
    {
        Direction facing = blockEntity.getCachedState().get(StirlingEngineBlock.FACING);
        matrices.push();
        BERenderUtils.rotateFacing(facing, matrices);

        matrices.translate(0.5, 0.5, 0.5);

        float delta = MinecraftClient.getInstance().isPaused() ? 0 : MinecraftClient.getInstance().getLastFrameDuration();
        blockEntity.angle = MathHelper.wrapDegrees(blockEntity.angle + blockEntity.getSpeed() * delta);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(blockEntity.angle));
        matrices.translate(-0.5, -0.5, -0.5);

        rotor.setTransform(matrices);

        matrices.pop();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), rotor);
    }
}
