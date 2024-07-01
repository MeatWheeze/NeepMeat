package com.neep.neepmeat.client.renderer;

import com.neep.meatlib.client.MeatlibModelManager;
import com.neep.meatweapons.client.renderer.meatgun.MeatgunModuleRenderer;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.component.CompressedAirComponent;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.item.RockDrillItem;
import com.neep.neepmeat.util.NMMaths;
import dev.monarkhes.myron_neepmeat.impl.client.model.MyronBakedModel;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class RockDrillItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer
{
    private final MinecraftClient client;

    public RockDrillItemRenderer(MinecraftClient client)
    {
        this.client = client;
    }

    private void renderItem(
            ItemStack stack,
            ModelTransformation.Mode renderMode,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            BakedModel model
    )
    {
        if (!stack.isEmpty())
        {
            matrices.push();

            if (model instanceof MyronBakedModel)
            {
                matrices.translate(0.5, 0, 0.5);
            }

            boolean bl2 = false;

            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(renderLayer);

            MeatgunModuleRenderer.renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);

            matrices.pop();
        }
    }

    @Override
    public void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay)
    {
        CompressedAirComponent component = NMComponents.COMPRESSED_AIR.getNullable(client.player);

        if (component != null)
        {
            boolean canUse = component.getAir() > 0;

            Identifier modelId = canUse ? NMExtraModels.ROCK_DRILL_ON : NMExtraModels.ROCK_DRILL_OFF;
            BakedModel main = ((MeatlibModelManager) client.getItemRenderer().getModels().getModelManager()).meatlib$getModel(modelId);

            BakedModel rod = ((MeatlibModelManager) client.getItemRenderer().getModels().getModelManager()).meatlib$getModel(NMExtraModels.ROCK_DRILL_ROD);
            if (mode == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND)
            {
                // Remove all the other transformations including the equip animation and display settings.
                // This is incredibly naughty as I don't know whether the transformations are supposed to be reused later.
                matrices.pop();
                matrices.pop();
                matrices.push();
                matrices.push();

                matrices.scale(1 / 16f, 1 / 16f, 1 / 16f);
                matrices.translate(-8, -6, -25);
                matrices.scale(16f, 16f, 16f);
                matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(
                        -15));

                renderItem(stack, mode, matrices, vertexConsumers, light, overlay, main);

                if (canUse && RockDrillItem.using(stack))
                    matrices.translate(0, 0, 3 / 16f * (1 + NMMaths.sin(client.world.getTime(), client.getTickDelta(), 10)));

                renderItem(stack, mode, matrices, vertexConsumers, light, overlay, rod);
            }
            else
            {
                renderItem(stack, mode, matrices, vertexConsumers, light, overlay, main);
            }
        }
    }
}
