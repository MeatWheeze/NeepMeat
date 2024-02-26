package com.neep.neepmeat.machine.large_motor;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class LargeMotorInstance extends BlockEntityInstance<LargeMotorBlockEntity> implements DynamicInstance
{
    private final MatrixStack matrices = new MatrixStack();

    private final ModelData rotorModel;

    public LargeMotorInstance(MaterialManager materialManager, LargeMotorBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        matrices.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());
        rotorModel = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LARGE_MOTOR_ROTOR).createInstance();
        rotorModel.loadIdentity().translate(getInstancePosition());
    }

    @Override
    protected void remove()
    {
        rotorModel.delete();
    }

    @Override
    public void beginFrame()
    {
        matrices.push();
        Direction facing = blockEntity.getCachedState().get(LargeMotorBlock.FACING);

        float delta = MinecraftClient.getInstance().isPaused() ? 0 : MinecraftClient.getInstance().getLastFrameDuration();
        blockEntity.currentSpeed = (float) (MathHelper.lerp(0.1, blockEntity.currentSpeed, blockEntity.getSpeed()));
        blockEntity.angle = MathHelper.wrapDegrees(blockEntity.angle + blockEntity.currentSpeed * delta);

        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0.5, 1.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(blockEntity.angle));
        matrices.translate(-0.5, -1.5, -0.5);

        rotorModel.setTransform(matrices);
        matrices.pop();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), rotorModel);
    }
}
