package com.neep.neepmeat.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.machine.reactor.disruptor.DisruptorSegmentBlock;
import com.neep.neepmeat.machine.reactor.disruptor.DisruptorSegmentBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class DisruptorSegmentInstance extends BlockEntityInstance<DisruptorSegmentBlockEntity> implements DynamicInstance
{
    private final ModelData centre;
    private final ModelData[] arms;

    private float rotSpeedRadians;
    private float rotation;

    public DisruptorSegmentInstance(MaterialManager materialManager, DisruptorSegmentBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);

        centre = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.DISRUPTOR_SEGMENT_CENTRE).createInstance();

        arms = new ModelData[4];
        for (int i = 0; i < 4; ++i)
        {
            arms[i] = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.DISRUPTOR_SEGMENT_ARM).createInstance();
        }
    }

    @Override
    protected void remove()
    {
        centre.delete();
        for (var arm : arms)
            arm.delete();
    }

    @Override
    public void beginFrame()
    {
        if (!MinecraftClient.getInstance().isPaused())
        {
            rotSpeedRadians = MathHelper.lerp(0.1f, rotSpeedRadians, blockEntity.progressIncrement() * 0.1f);
            rotation += rotSpeedRadians;
        }

        Direction.Axis axis = blockEntity.getCachedState().get(DisruptorSegmentBlock.AXIS);

        Direction toFace = Direction.get(Direction.AxisDirection.POSITIVE, axis);

        centre.loadIdentity()
                .translate(getInstancePosition())
                .centre()
                .multiply(getRotation(axis))
                .rotate(Direction.UP, rotation)
                .unCentre()
                ;


        for (int i = 0; i < arms.length; ++i)
        {
            arms[i].loadIdentity().
                    translate(getInstancePosition())
                    .centre()
                    .multiply(getRotation(axis))
                    .rotate(Direction.UP, i / 4f * MathHelper.TAU + rotation)
                    .unCentre()
            ;
        }
    }

    private Quaternionf getRotation(Direction.Axis axis)
    {
        return switch (axis)
        {
            case X -> RotationAxis.NEGATIVE_Z.rotationDegrees(90);
            case Y -> new Quaternionf();
            case Z -> RotationAxis.NEGATIVE_X.rotationDegrees(90);
        };
    }

    @Override
    public void updateLight()
    {
        super.updateLight();
        relight(getWorldPosition(), arms);
        relight(getWorldPosition(), centre);
    }
}
