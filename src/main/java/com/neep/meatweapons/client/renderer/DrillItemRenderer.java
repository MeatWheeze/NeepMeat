package com.neep.meatweapons.client.renderer;

import com.eliotlash.mclib.math.functions.limit.Min;
import com.neep.meatweapons.client.model.DrillItemModel;
import com.neep.meatweapons.item.AssaultDrillItem;
import com.neep.neepmeat.machine.small_trommel.SmallTrommelBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class DrillItemRenderer extends GeoItemRenderer<AssaultDrillItem>
{
    public DrillItemRenderer(DrillItemModel model)
    {
        super(model);
    }

    boolean b;

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay)
    {
        NbtCompound nbt = stack.getOrCreateNbt();

        MinecraftClient client = MinecraftClient.getInstance();
        float timeDegrees = client.world.getTime() % 360 + client.getTickDelta();

        boolean using = nbt.getBoolean("using");

        if (b && !using)
        {
            System.out.println("End");
        }

        b = using;

        float thrust = nbt.getFloat("thrust");
        float targetThrust = using ? 0.07f : 0;

        float delta = using ? 0.1f : 0.05f;
        float angle = nbt.getFloat("angle");
        float currentSpeed = nbt.getFloat("currentSpeed");
        float targetSpeed = using ? 3 : 0;

        float shake = (float) (0.01f * Math.sin(Math.toRadians(timeDegrees) * 140));
        if (!using) shake = 0;

        thrust = MathHelper.lerp(0.1f, thrust, targetThrust);
        currentSpeed = (MathHelper.lerp(delta, currentSpeed, targetSpeed));
        angle = MathHelper.wrapDegrees(angle + currentSpeed * MinecraftClient.getInstance().getLastFrameDuration());

        nbt.putFloat("currentSpeed", currentSpeed);
        nbt.putFloat("angle", angle);
        nbt.putFloat("thrust", thrust);

        float finalAngle = angle;
        GeckoLibCache.getInstance().parser.setValue("angle", () -> finalAngle);

        poseStack.translate(-thrust / 2, shake, -thrust);

        super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}