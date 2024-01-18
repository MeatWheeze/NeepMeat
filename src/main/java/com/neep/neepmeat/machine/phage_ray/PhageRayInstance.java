package com.neep.neepmeat.machine.phage_ray;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class PhageRayInstance extends EntityInstance<PhageRayEntity> implements DynamicInstance
{
    private final ModelData base;
    private final ModelData barrel;
    private final MatrixStack matrices = new MatrixStack();

    public PhageRayInstance(MaterialManager materialManager, PhageRayEntity entity)
    {
        super(materialManager, entity);
        this.base = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.PHAGE_RAY_BASE).createInstance();
        this.barrel = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.PHAGE_RAY_BARREL).createInstance();
    }

    @Override
    protected void remove()
    {
        base.delete();;
        barrel.delete();
    }

    @Override
    public void beginFrame()
    {
        matrices.push();
        matrices.translate(getInstancePosition().getX(), getInstancePosition().getY() - 0.5, getInstancePosition().getZ());

        float tickDelta = MinecraftClient.getInstance().getTickDelta();;
        float pitch = entity.getPitch(tickDelta);
        float yaw = entity.getYaw(tickDelta);

        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(yaw));
        matrices.translate(-0.5, 0, -0.5);

        base.setTransform(matrices);

        matrices.translate(0, 2, 0.5);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
        matrices.translate(0, -2, -0.5);

        barrel.setTransform(matrices);

        matrices.pop();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition().up(), base);
        relight(getWorldPosition().up(), barrel);
    }
}
