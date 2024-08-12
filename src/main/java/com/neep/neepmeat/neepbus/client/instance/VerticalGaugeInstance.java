package com.neep.neepmeat.neepbus.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.TickableInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.neepbus.block.VerticalGaugeBlock;
import com.neep.neepmeat.neepbus.block.entity.VerticalGaugeBlockEntity;
import net.minecraft.util.math.Direction;

public class VerticalGaugeInstance extends BlockEntityInstance<VerticalGaugeBlockEntity> implements TickableInstance
{
    private final ModelData indicator;

    public VerticalGaugeInstance(MaterialManager materialManager, VerticalGaugeBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        indicator = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.VERTICAL_GAUGE_INDICATOR).createInstance();
    }

    @Override
    protected void remove()
    {
        indicator.delete();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), indicator);
    }

    @Override
    public void tick()
    {
        float length = 12 / 16f;

        float f = LinearLeverInstance.clamp(
                (float) blockEntity.getValue() / (blockEntity.getMaxValue() - blockEntity.getMinValue()),
                0, 1);

        Direction facing = blockState.get(VerticalGaugeBlock.FACING);
        indicator
                .loadIdentity()
                .translate(getInstancePosition())
                .centre()
                .rotateToFace(facing)
                .translate(0, -length / 2, 0)
                .scale(1, f, 1)
                .translate(0, length / 2, 0)
                .unCentre()
                ;
    }
}
