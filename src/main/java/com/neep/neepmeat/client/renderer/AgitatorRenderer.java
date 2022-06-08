package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.block.machine.AgitatorBlock;
import com.neep.neepmeat.blockentity.DeployerBlockEntity;
import com.neep.neepmeat.blockentity.machine.AgitatorBlockEntity;
import com.neep.neepmeat.client.NMExtraModels;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class AgitatorRenderer implements BlockEntityRenderer<AgitatorBlockEntity>
{
    public AgitatorRenderer(BlockEntityRendererFactory.Context context)
    {
    }

    @Override
    public void render(AgitatorBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        matrices.push();


        matrices.push();
        Direction facing = be.getCachedState().get(AgitatorBlock.FACING);

//        BERenderUtils.rotateFacing(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        if (facing == Direction.DOWN)
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));

        matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion((be.getWorld().getTime() + tickDelta) / 10f));
        matrices.translate(-0.5, -0.5, -0.5);

        matrices.translate(0, 1, 0);
        BERenderUtils.renderModel(NMExtraModels.AGITATOR_BLADES, matrices, be.getWorld(), be.getPos(), be.getCachedState(), vertexConsumers);

        matrices.pop();


        matrices.pop();
    }
}