package com.neep.meatweapons.client.renderer;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.meatweapons.client.meatgun.RecoilManager;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunModuleRenderer;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunModuleRenderers;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunParticleManager;
import com.neep.meatweapons.init.MWComponents;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.mixin.HeldItemRendererAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class MeatgunPistolRenderer extends BuiltinModelItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer
{
    private final MinecraftClient client;

    public MeatgunPistolRenderer()
    {
        super(MinecraftClient.getInstance().getBlockEntityRenderDispatcher(), MinecraftClient.getInstance().getEntityModelLoader());
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vcp, int light, int overlay)
    {
        // Remove the transformations applied by ItemRenderer
        matrices.pop();
        matrices.push();

        AbstractClientPlayerEntity player = client.player;
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(player);

        boolean mainHand = ((HeldItemRendererAccessor) client.gameRenderer.firstPersonRenderer).getMainHand().equals(stack);
        boolean leftHanded = mode.isFirstPerson()
                && (player.getMainArm() == Arm.LEFT && mainHand || player.getMainArm() == Arm.RIGHT && !mainHand);

        // Apply the display transformations
        Transformation transformation = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(stack).getTransformation().getTransformation(mode);
        transformation.apply(leftHanded, matrices);
        matrices.translate(-0.5F, -0.5F, -0.5F);

        renderInner(stack, player, playerEntityRenderer, mode, matrices, vcp, true, mainHand, leftHanded, AnimationTickHolder.getPartialTicks(), light, overlay);
    }

    // It's easier to override this one
    protected void renderInner(ItemStack stack, AbstractClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vcp, boolean showArm, boolean mainHand, boolean leftHanded, float tickDelta, int light, int overlay)
    {
        // Step recoil
        MeatgunComponent component = MWComponents.MEATGUN.get(stack);
        RecoilManager recoil = component.getRecoil();
        if (mode.isFirstPerson())
        {
            float lastFrame = !client.isPaused() ? client.getLastFrameDuration() : 0;

            float prevHorAmount = recoil.horAmount;
            recoil.horAmount = recoil.horAmount - Math.signum(recoil.horAmount) * recoil.horReturnSpeed * lastFrame;
            if (Math.signum(prevHorAmount) != Math.signum(recoil.horAmount))
                recoil.horReturnSpeed = 0;

            float prevAmount = recoil.amount;
            recoil.amount = recoil.amount - Math.signum(recoil.amount) * recoil.returnSpeed * lastFrame;
            if (Math.signum(prevAmount) != Math.signum(recoil.amount))
                recoil.returnSpeed = 0;

            transformRecoil(matrices, recoil);
        }

        Matrix4f firstPersonModelTransform = getFirstPersonModelTransform();

        // Recursive module rendering
        matrices.push();
        if (mode.isFirstPerson())
            matrices.multiplyPositionMatrix(firstPersonModelTransform);

        MeatgunModule root = component.getRoot().root;
        renderRecursive(matrices, root, stack, component, mode, vcp,
                MinecraftClient.getInstance().world.getTime(),
                MinecraftClient.getInstance().getTickDelta(), light, overlay);
        matrices.pop();

        renderParticles(matrices, mode, firstPersonModelTransform, vcp, light, overlay);

        if (showArm)
            renderArms(matrices, mode, player, playerEntityRenderer, vcp, light, mainHand, leftHanded);
    }

    protected void transformRecoil(MatrixStack matrices, RecoilManager recoil)
    {
        matrices.translate(0, 0, recoil.horAmount);
        matrices.translate(0, 0, 1.4);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(recoil.amount));
        matrices.translate(0, 0, -1.4);
    }

    protected Matrix4f getFirstPersonModelTransform()
    {
        float modelScale = 1.2f;
        return new Matrix4f()
                .scaleAround(modelScale, modelScale, modelScale, 8 / 16f, 0 / 16f, 0 / 16f)
                .translate(0, 3.5f / 16f, -9 / 16f)
                ;
    }

    protected void renderParticles(MatrixStack matrices, ModelTransformationMode mode, Matrix4f modelTransform, VertexConsumerProvider vcp, int light, int overlay)
    {
        if (mode.isFirstPerson())
        {
            matrices.push();
            matrices.multiplyPositionMatrix(modelTransform);
            Camera camera = client.gameRenderer.getCamera();
            matrices.translate(0.5, 0, 1); // No idea why this transform is necessary, but this puts (0,0,0) at (8,0,16) in model coords.

            VertexConsumer consumer = vcp.getBuffer(RenderLayer.getEntityTranslucent(SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE));
            for (var particle : MeatgunParticleManager.getParticles())
            {
                particle.render(matrices, camera, consumer, overlay, client.getTickDelta());
            }
            matrices.pop();
        }
    }

    protected void renderArms(MatrixStack matrices, ModelTransformationMode mode, AbstractClientPlayerEntity player, PlayerEntityRenderer playerEntityRenderer, VertexConsumerProvider vcp, int light, boolean mainHand, boolean leftHanded)
    {
        Hand hand = mainHand ? Hand.MAIN_HAND : Hand.OFF_HAND;
        Hand otherHand = mainHand ? Hand.OFF_HAND : Hand.MAIN_HAND;

        float armScale = 1.4f;

        float cx = -4 / 16f;
        float cy = 4 / 16f;
        float cz = (2 - 10) / 16f;

        Matrix4f t = new Matrix4f()
                .translate(cx, cy, cz)
                .scale(armScale, armScale, armScale)
                .translate(-cx, -cy, -cz)
                .translate((leftHanded ? 0 : 12) / 16f / armScale, -4 / 16f / armScale, 20 / 16f / armScale)
                ;
        matrices.multiplyPositionMatrix(t);

        if (mode.isFirstPerson())
//                && player.getStackInHand(otherHand).isEmpty())
//                && hand == Hand.MAIN_HAND) // Prevent three arms when the main hand is empty
        {
            if (leftHanded)
            {
                renderArm(playerEntityRenderer.getModel().leftArm, true, matrices, player, vcp, light);
                renderArm(playerEntityRenderer.getModel().leftSleeve, true, matrices, player, vcp, light);
            }
            else
            {
                renderArm(playerEntityRenderer.getModel().rightArm, false, matrices, player, vcp, light);
                renderArm(playerEntityRenderer.getModel().rightSleeve, false, matrices, player, vcp, light);
            }
        }
    }

    private void renderArm(ModelPart armPart, boolean leftHanded, MatrixStack matrices, AbstractClientPlayerEntity player, VertexConsumerProvider vcp, int light)
    {
//        armPart.pitch = (float) Math.toRadians(-120);
        armPart.pitch = (float) Math.toRadians(-90f);
        armPart.yaw = 0;
//        armPart.yaw = (leftHanded ? -1 : 1) * MathHelper.PI / 6;
        armPart.roll = (float) Math.PI;
        armPart.render(matrices, vcp.getBuffer(RenderLayer.getEntityCutout(player.getSkinTexture())),
                light, OverlayTexture.DEFAULT_UV);
    }

    private <T extends MeatgunModule> void renderRecursive(MatrixStack matrices, T module, ItemStack stack, MeatgunComponent component,
                                                           ModelTransformationMode mode, VertexConsumerProvider vcp, long time, float tickDelta, int light, int overlay)
    {
        if (module == MeatgunModule.DEFAULT)
            return;

        MeatgunModuleRenderer<T> renderer = MeatgunModuleRenderers.get(module);
        renderer.render(stack, component, module, mode, matrices, vcp, time, tickDelta, light, overlay);

        for (var child : module.getChildren())
        {
            matrices.push();
            matrices.translate(0.5, 0, 1);
            Matrix4f matrix4f = child.transform(tickDelta);
            matrices.multiplyPositionMatrix(matrix4f);
            matrices.translate(-0.5, 0, -1);
            renderRecursive(matrices, child.get(), stack, component, mode, vcp, time, tickDelta, light, overlay);
            matrices.pop();
        }
    }

}
