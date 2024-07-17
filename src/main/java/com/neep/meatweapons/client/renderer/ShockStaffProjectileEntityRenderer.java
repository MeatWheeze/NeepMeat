package com.neep.meatweapons.client.renderer;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.entity.ShockStaffProjectileEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@Environment(EnvType.CLIENT)
public class ShockStaffProjectileEntityRenderer extends EntityRenderer<ShockStaffProjectileEntity>
{
    private static final Identifier TEXTURE = new Identifier(MeatWeapons.NAMESPACE, "textures/entity/shock_staff_projectile.png");

    private static final float MIN_DISTANCE = 12.25F;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public ShockStaffProjectileEntityRenderer(EntityRendererFactory.Context ctx, float scale, boolean lit)
    {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
    }

    public ShockStaffProjectileEntityRenderer(EntityRendererFactory.Context context)
    {
        this(context, 1.0F, false);
    }

    @Override
    protected int getBlockLight(ShockStaffProjectileEntity entity, BlockPos pos)
    {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }

    @Override
    public void render(ShockStaffProjectileEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light)
    {
        if (entity.age >= 2 || !(this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(entity) < 12.25))
        {
            matrices.push();
            matrices.scale(this.scale, this.scale, this.scale);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
//            this.itemRenderer
//                    .renderItem(
//                            Items.SNOWBALL.getDefaultStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), entity.getId()
//                    );

//            QuadEmitter emitter = RendererAccess.INSTANCE.getRenderer().meshBuilder().getEmitter();
            VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(getTexture(entity)));

//            float f = (float) entity.getX();
//            float g = (float) entity.getY();
//            float h = (float) entity.getX();
            float f = 0;
            float g = 0;
            float h = 0;

            float i = 0.5f;
            Vector3f[] vector3fs = new Vector3f[]{
                    new Vector3f(-1.0F, -1.0F, 0F),
                    new Vector3f(-1.0F, 1.0F, 0F),
                    new Vector3f(1.0F, 1.0F, 0F),
                    new Vector3f(1.0F, -1.0F, 0F)};

            for (int j = 0; j < 4; ++j)
            {
                Vector3f vector3f = vector3fs[j];
                vector3f.mul(i);
                vector3f.add(f, g, h);

                var v4 = new Vector4f(vector3f, 1);
                Matrix4f mat = new Matrix4f(matrices.peek().getPositionMatrix());
                v4.mul(mat);
                vector3f.set(v4.x, v4.y, v4.z);
            }

            float k = 0;
            float l = 16 / 64f;
            float m = 0;
            float n = 16 / 64f;

            float red = 1;
            float green = 1;
            float blue = 1;
            float alpha = 1;

            int overlay = OverlayTexture.DEFAULT_UV;

            consumer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z())
                    .color(red, green, blue, alpha)
                    .texture(k, m)
                    .overlay(overlay)
                    .light(light)
                    .normal(0, 1, 0)
                    .next();
            consumer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z())
                    .color(red, green, blue, alpha)
                    .texture(l, m)
                    .overlay(overlay)
                    .light(light)
                    .normal(0, 1, 0)
                    .next();
            consumer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z())
                    .color(red, green, blue, alpha)
                    .texture(l, n)
                    .overlay(overlay)
                    .light(light)
                    .normal(0, 1, 0)
                    .next();
            consumer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z())
                    .color(red, green, blue, alpha)
                    .texture(k, n)
                    .overlay(overlay)
                    .light(light)
                    .normal(0, 1, 0)
                    .next();

            matrices.pop();
            super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }

    @Override
    public Identifier getTexture(ShockStaffProjectileEntity entity)
    {
        return TEXTURE;
    }
}
