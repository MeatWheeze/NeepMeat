package com.neep.neepmeat.machine.mixer;

import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import com.neep.neepmeat.client.renderer.MultiFluidRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(value = EnvType.CLIENT)
@SuppressWarnings("UnstableApiUsage")
public class MixerRenderer implements BlockEntityRenderer<MixerBlockEntity>
{

    public MixerRenderer(BlockEntityRendererFactory.Context ctx)
    {
    }

    @Override
    public void render(MixerBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();
        matrices.translate(0, 1 - 5f / 16f, 0);
        float progress = 0;
        float nextOutput = 0;
        if (be.getCurrentRecipe() != null && be.progressIncrement() > be.minIncrement())
        {
            progress = (be.progress) / be.processLength;
            nextOutput = progress * be.getCurrentRecipe().fluidOutput.amount();
        }


        WritableSingleFluidStorage storage = (WritableSingleFluidStorage) be.storage.getFluidOutput();
        storage.renderLevel = MathHelper.lerp(0.1f, storage.renderLevel,(storage.getAmount() + nextOutput) / (float) storage.getCapacity());
        float outputEnd = storage.renderLevel;
        FluidVariant output = storage.getResource();

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.9f, 1, 0.9f);
        matrices.translate(-0.5, -0.5, -0.5);
        MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, output, 0, outputEnd, outputEnd, 1, light);

        if (be.getCurrentRecipe() != null)
        {
            FluidVariant var1 = be.storage.displayInput1;
            FluidVariant var2 = be.storage.displayInput2;

            float scale = (-Math.abs(2 * progress - 1) + 1) * 0.1f;

            MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var1, outputEnd, outputEnd + scale, outputEnd + scale, 1, light);
            MultiFluidRenderer.renderFluidCuboid(vertexConsumers, matrices, var2, outputEnd + scale, outputEnd + scale * 2, outputEnd + scale * 2, 1, light);
        }
        matrices.pop();
        matrices.pop();

        matrices.push();
        matrices.translate(0.5, 1.5, 0.5);

        float delta = MinecraftClient.getInstance().isPaused() ? 0 : MinecraftClient.getInstance().getLastFrameDuration();

        be.bladeSpeed = MathHelper.lerp(0.5f, be.bladeSpeed,
                be.currentRecipe == null || be.progressIncrement() <= be.minIncrement() ? 0 : 50f * be.progressIncrement()
        );

        be.bladeAngle = MathHelper.wrapDegrees(be.bladeAngle + be.bladeSpeed * delta);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(be.bladeAngle));
        matrices.translate(-0.5, -0.5, -0.5);
        BERenderUtils.renderModel(NMExtraModels.MIXER_AGITATOR_BLADES, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        renderItems(be, matrices, 0, 0, outputEnd - 0.4f, 0.4f, tickDelta, vertexConsumers, overlay);
        matrices.pop();
    }

    public static void renderItems(MixerBlockEntity be, MatrixStack matrices, float angle, float angleOffset, float fluidHeight, float offset, float tickDelta, VertexConsumerProvider vertexConsumers, int overlay)
    {

        WritableStackStorage view = be.getItemStorage(null);
        matrices.push();
        matrices.translate(0.5, 0, 0.5);

        int n = (int) MathHelper.clamp(view.amount / 14f, 1, 3);

        for (int i = 0; i < n; ++i)
        {
            float bobOffset = (float) ((float) i * (2 / (float) n * Math.PI));
            float yOffset = (float) MathHelper.clamp(fluidHeight + 0.03f * Math.sin((be.getWorld().getTime() + tickDelta) / 6 + bobOffset), -0.27f, 1 - 0.4f);

            matrices.push();
            matrices.multiply(Quaternion.fromEulerXyz(0, (float) (angle + angleOffset), 0));
            matrices.translate(0, yOffset, offset);
            MinecraftClient.getInstance().getItemRenderer()
                    .renderItem(view.getResource().toStack((int) view.getAmount()), ModelTransformation.Mode.GROUND, 255, overlay, matrices, vertexConsumers, 0);
            angleOffset += 2 * Math.PI / n;
            matrices.pop();
        }
        matrices.pop();
    }
}