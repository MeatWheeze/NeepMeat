package com.neep.neepmeat.machine.fabricator;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.client.NMExtraModels;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.stream.IntStream;

public class FabricatorInstance extends BlockEntityInstance<FabricatorBlockEntity> implements DynamicInstance
{
    private final ModelData rotor;
    private final List<ModelData> segments;

    public FabricatorInstance(MaterialManager materialManager, FabricatorBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        rotor = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.FABRICATOR_ROTOR).createInstance();
        segments = IntStream.range(0, 8).mapToObj(i ->
                materialManager.defaultCutout().material(Materials.TRANSFORMED).getModel(NMExtraModels.FABRICATOR_SEGMENT).createInstance()).toList();
    }

    @Override
    protected void remove()
    {
        rotor.delete();
        for (var segment : segments)
        {
            segment.delete();
        }
    }

    @Override
    public void beginFrame()
    {
        float mainAngle = (AnimationTickHolder.getRenderTime() * 2) * MathHelper.RADIANS_PER_DEGREE;

        rotor.loadIdentity()
                .translate(getInstancePosition())
                .translate(0, 1, 0)
                .rotateCentered(Direction.UP, mainAngle)
        ;

        for (int i = 0; i < segments.size(); ++i)
        {
            float angle = MathHelper.RADIANS_PER_DEGREE * (((i / 8f) * 360) % 360) + mainAngle;

            float yOffset = 0.05f * (MathHelper.sin(angle * angle / 10) + 1);

            segments.get(i)
                    .loadIdentity()
                    .translate(getInstancePosition())
                    .translate(0, 1 - yOffset , 0)
                    .rotateCentered(Direction.UP, angle)
                    ;
        }
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), segments.stream());
        relight(getWorldPosition(), rotor);
    }
}
