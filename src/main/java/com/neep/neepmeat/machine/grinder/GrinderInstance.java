package com.neep.neepmeat.machine.grinder;

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

@Environment(value= EnvType.CLIENT)
public class GrinderInstance extends BlockEntityInstance<GrinderBlockEntity> implements DynamicInstance
{
    private final ModelData jawModel;
    private final MatrixStack matrixStack;

    public GrinderInstance(MaterialManager materialManager, GrinderBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);

        matrixStack = new MatrixStack();
        matrixStack.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());

        jawModel = materialManager.defaultSolid().material(Materials.TRANSFORMED)
                .getModel(NMExtraModels.P_CRUSHER_JAW)
                .createInstance();

        jawModel.loadIdentity().translate(getInstancePosition());
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), jawModel);
    }

    @Override
    public void beginFrame()
    {
        float tickDelta = MinecraftClient.getInstance().getTickDelta();
        double sinTime = Math.sin(blockEntity.getWorld().getTime()) * Math.cos(tickDelta) + Math.cos(blockEntity.getWorld().getTime()) * Math.sin(tickDelta);

        matrixStack.push();
        Direction facing = blockEntity.getCachedState().get(GrinderBlock.FACING);

        if (blockEntity.currentRecipe != null && blockEntity.progressIncrement() > 0)
        {
            var unit = facing.getUnitVector();
            double magnitude = Math.abs(0.05 * sinTime);
            matrixStack.translate(unit.getX() * magnitude, 0, unit.getZ() * magnitude);
        }
        BERenderUtils.rotateFacing(facing, matrixStack);
        jawModel.setTransform(matrixStack);
        matrixStack.pop();
    }

    @Override
    protected void remove()
    {
        jawModel.delete();
    }
}
