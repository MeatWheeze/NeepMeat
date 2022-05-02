package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.block.BaseHorFacingBlock;
import com.neep.neepmeat.block.redstone.BigLeverBlock;
import com.neep.neepmeat.blockentity.BigLeverBlockEntity;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.NeepMeatClient;
import com.neep.neepmeat.client.model.GlassTankModel;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.Random;

public class BigLeverRenderer<T extends BigLeverBlockEntity> implements BlockEntityRenderer<T>
{
    Model model;

    public BigLeverRenderer(BlockEntityRendererFactory.Context context)
    {
        model = new GlassTankModel(context.getLayerModelPart(NeepMeatClient.MODEL_GLASS_TANK_LAYER));
    }

    @Override
    public void render(T be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        Direction facing = be.getCachedState().get(BaseHorFacingBlock.FACING);
        WallMountLocation face = be.getCachedState().get(BigLeverBlock.FACE);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(facing.asRotation()));

        switch (face)
        {
            case FLOOR:
                matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((float) (Math.PI)));
                break;
            case WALL:
                matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) (Math.PI / 2)));
                break;
            case CEILING:
                matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion((float) (Math.PI)));
                break;
        }

        matrices.translate(0, -0.2, 0);
//        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(be.getWorld().getTime() + tickDelta * 1));
        boolean switched = !be.getWorld().getBlockState(be.getPos()).get(BigLeverBlock.POWERED);
        float angle = 20;

        if (be.activeTicks > 0)
            angle = (float) be.tickCounter / be.activeTicks * 20;

        be.leverDelta = (float) MathHelper.lerp(0.1, be.leverDelta, switched ? 0 : angle);
//        be.leverDelta = switched ? 0 : angle;
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(be.leverDelta));
        matrices.translate(0, 0.2, 0);
        matrices.translate(-0.5, -0.5, -0.5);

        BakedModelManager manager = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelManager();
        BakedModel handle = BakedModelManagerHelper.getModel(manager, NMExtraModels.BIG_LEVER_HANDLE);
        BlockModelRenderer renderer = MinecraftClient.getInstance().getBlockRenderManager().getModelRenderer();
        renderer.render(
                be.getWorld(),
                handle,
                be.getCachedState(),
                be.getPos(),
                matrices,
                vertexConsumers.getBuffer(RenderLayer.getCutout()),
                true,
                new Random(0),
                0,
                0
        );

//        ItemStack stack = be.getResource().toStack((int) be.getAmount());

//        be.stackRenderDelta = MathHelper.lerp(delta, be.stackRenderDelta, be.getAmount() <= 0 ? 0.3f : 0f);
//        matrices.translate(0.5, 0.25f, 0.5);
//        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((be.getWorld().getTime() + tickDelta) * 1));


        matrices.pop();
    }
}