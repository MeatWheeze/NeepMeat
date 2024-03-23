package com.neep.neepmeat.machine.large_crusher;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class LargeCrusherInstance extends BlockEntityInstance<LargeCrusherBlockEntity> implements DynamicInstance
{
    private final ModelData jawModel;
    private final ModelData sheathModel;
    private final MatrixStack matrixStack;

    public LargeCrusherInstance(MaterialManager materialManager, LargeCrusherBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        this.jawModel = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LARGE_CRUSHER_JAW).createInstance();
        this.sheathModel = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LARGE_CRUSHER_SHEATH).createInstance();

        matrixStack = new MatrixStack();
        matrixStack.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());
    }

    @Override
    public void beginFrame()
    {
        float tickDelta = AnimationTickHolder.getPartialTicks();
        float sinTime1 = NMMaths.sin(blockEntity.getWorld().getTime(), tickDelta, 1);
        float sinTime2 = NMMaths.sin(blockEntity.getWorld().getTime(), tickDelta, 2);

        matrixStack.push();
        Direction facing = blockEntity.getCachedState().get(LargeCrusherBlock.FACING);

//        boolean recipe = false;
//        for (var slot : blockEntity.getSlots())
//        {
//            recipe = recipe || slot.getRecipe() != null;
//        }

        if (blockEntity.progressIncrement() > blockEntity.minIncrement())
        {
            var unit = facing.getUnitVector();
            double magnitude = Math.abs(0.05 * sinTime1);
            matrixStack.translate(unit.x * magnitude, 0, unit.z * magnitude);
        }

        BERenderUtils.rotateFacing(facing, matrixStack);
        jawModel.setTransform(matrixStack);
        matrixStack.pop();

        matrixStack.push();
        if (blockEntity.progressIncrement() > blockEntity.minIncrement())
        {
            matrixStack.translate(0, sinTime2 * 0.03, 0);
        }

        BERenderUtils.rotateFacing(facing, matrixStack);
        sheathModel.setTransform(matrixStack);
        matrixStack.pop();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), jawModel, sheathModel);
    }

    @Override
    protected void remove()
    {
        jawModel.delete();
        sheathModel.delete();
    }
}
