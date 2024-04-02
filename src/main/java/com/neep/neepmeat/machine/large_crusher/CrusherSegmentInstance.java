package com.neep.neepmeat.machine.large_crusher;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.machine.live_machine.block.entity.CrusherSegmentBlockEntity;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class CrusherSegmentInstance extends BlockEntityInstance<CrusherSegmentBlockEntity> implements DynamicInstance
{
    private final ModelData sheathModel;
    private final MatrixStack matrixStack;

    public CrusherSegmentInstance(MaterialManager materialManager, CrusherSegmentBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        this.sheathModel = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.CRUSHER_SEGMENT_SHEATH).createInstance();

        matrixStack = new MatrixStack();
        matrixStack.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());
    }

    @Override
    public void beginFrame()
    {
        float tickDelta = AnimationTickHolder.getPartialTicks();
        float sinTime2 = NMMaths.sin(blockEntity.getWorld().getTime(), tickDelta, 2);

        matrixStack.push();
        if (blockEntity.progressIncrement() > blockEntity.minIncrement())
        {
            matrixStack.translate(0, sinTime2 * 0.03, 0);
        }

        sheathModel.setTransform(matrixStack);
        matrixStack.pop();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), sheathModel);
    }

    @Override
    protected void remove()
    {
        sheathModel.delete();
    }
}
