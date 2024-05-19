package com.neep.neepmeat.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.machine.live_machine.block.LuckyOneBlock;
import com.neep.neepmeat.machine.live_machine.block.entity.LuckyOneBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class LuckyOneInstance extends BlockEntityInstance<LuckyOneBlockEntity> implements DynamicInstance
{
    private final ModelData head;
    private final ModelData arm_l;
    private final ModelData arm_r;
    private final ModelData body;

    private final MatrixStack matrixStack = new MatrixStack();

    public LuckyOneInstance(MaterialManager materialManager, LuckyOneBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        head = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LUCKY_ONE_HEAD).createInstance();
        arm_l = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LUCKY_ONE_ARM_L).createInstance();
        arm_r = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LUCKY_ONE_ARM_R).createInstance();
        body = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LUCKY_ONE_BODY).createInstance();

        matrixStack.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());
    }

    @Override
    protected void remove()
    {
        head.delete();
        arm_l.delete();
        arm_r.delete();
        body.delete();
    }

    @Override
    public void beginFrame()
    {
        int offset = blockEntity.animationOffset;
        float time = AnimationTickHolder.getRenderTime();

        float headYaw = 0;
        float headPitch = 4;
        float bodyPitch = 0;

        if (blockEntity.isActive())
        {
            headYaw = 2 * MathHelper.sin((MathHelper.sin(time + offset) + 1) * time / (10));
            headPitch = 8 * MathHelper.cos(time / 4) * MathHelper.sin((time - offset) / 10 + 2);

            bodyPitch = MathHelper.clamp(
                    5 * (MathHelper.cos((time + offset / 2f) / 4 + 2)
                            + MathHelper.sin(time / 10 + 1) * MathHelper.sin(time * (offset / 100f - 0.5f)))
                            + MathHelper.cos(time),
                    0, 20);
        }

        BlockState state = blockEntity.getCachedState();

        matrixStack.push();
        matrixStack.translate(0.5, 1.5, 0.5);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - state.get(LuckyOneBlock.FACING).asRotation()));
        matrixStack.translate(0, 0, 2 / 16f);
        matrixStack.translate(0, 0, -0.5);
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-8 - bodyPitch));

        matrixStack.translate(-0.5, 0, 0);

        matrixStack.translate(0, -1.5, 0);
        body.setTransform(matrixStack);

        matrixStack.push();
        matrixStack.translate(0.5, 1.5 + (12 / 16f), 0);
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(headPitch));
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(headYaw));
        matrixStack.translate(-0.5, -(1.5 + (12 / 16f)), 0);
        head.setTransform(matrixStack);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(0.5 - (4 / 16f), 1.5 + (12 / 16f), 0);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(3));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-6 - bodyPitch));
        matrixStack.translate(-(0.5 - 4 / 16f), -(1.5 + (12 / 16f)), 0);
        arm_l.setTransform(matrixStack);
        matrixStack.pop();

        matrixStack.push();
        matrixStack.translate(0.5 + (4 / 16f), 1.5 + (12 / 16f), 0);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-3));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-6 - bodyPitch));
        matrixStack.translate(-(0.5 + 4 / 16f), -(1.5 + (12 / 16f)), 0);
        arm_r.setTransform(matrixStack);
        matrixStack.pop();

        matrixStack.pop();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), head, arm_l, arm_r, body);
    }
}
