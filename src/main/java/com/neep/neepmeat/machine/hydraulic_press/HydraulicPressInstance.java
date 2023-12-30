package com.neep.neepmeat.machine.hydraulic_press;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class HydraulicPressInstance extends BlockEntityInstance<HydraulicPressBlockEntity> implements DynamicInstance
{
    public static final float MAX_DISPLACEMENT = 8 / 16f + 0.04f;

    private final ModelData arm;

    public HydraulicPressInstance(MaterialManager materialManager, HydraulicPressBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);

        this.arm = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.HYDRAULIC_PRESS_ARM).createInstance();
    }

    @Override
    protected void remove()
    {
        arm.delete();
    }

    @Override
    public void beginFrame()
    {
        WritableSingleFluidStorage storage = blockEntity.getStorage(null);
        float extension = storage.getAmount() / (float) HydraulicPressBlockEntity.EXTEND_AMOUNT;
        blockEntity.renderExtension = MathHelper.lerp(0.1f, blockEntity.renderExtension, extension);

        arm.loadIdentity().translate(getInstancePosition()).translate(0, - MAX_DISPLACEMENT * blockEntity.renderExtension, 0);
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), arm);
    }
}
