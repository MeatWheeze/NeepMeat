package com.neep.neepmeat.client.fluid;

import com.neep.neepmeat.api.processing.OreFatRegistry;
import com.neep.neepmeat.mixin.SpriteContentsAccessor;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Environment(EnvType.CLIENT)
public class OreFatFluidVariantRenderHandler implements FluidVariantRenderHandler
{
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final Object2IntMap<NbtCompound> map = new Object2IntOpenHashMap<>();

    @Override
    public int getColor(FluidVariant fluidVariant, @Nullable BlockRenderView view, @Nullable BlockPos pos)
    {
        NbtCompound nbt = fluidVariant.getNbt();
        if (nbt != null)
        {
            return getTint(nbt);
        }
        return -1;
    }

    private int getTint(NbtCompound nbt)
    {
        return map.computeIfAbsent(nbt, this::createTint);
    }

    /**
     * @param nbt Ore fat NBT root tag
     * @return The average colour of non-transparent pixels. -1 if invalid.
     */
    private int createTint(NbtCompound nbt)
    {
        if (nbt != null)
        {
            OreFatRegistry.Entry entry;
            if ((entry = OreFatRegistry.get(nbt)) != null)
            {
                Item item = entry.result().getItem();

                BakedModel model = client.getItemRenderer().getModels().getModel(item);
                if (model != null)
                {
                    Sprite sprite = model.getParticleSprite();

                    if (sprite != null)
                    {
                        NativeImage image = ((SpriteContentsAccessor) sprite.getContents()).getImage();

                        int pixels = 0;
                        int cumR = 0;
                        int cumG = 0;
                        int cumB = 0;

                        for (int i = 0; i < image.getWidth(); ++i)
                        {
                            for (int j = 0; j < image.getHeight(); ++j)
                            {
                                int agbr = image.getColor(i, j);
                                int alpha = agbr >> 24;
                                if (alpha != 0)
                                {
                                    cumR += agbr & 0xff;
                                    cumG += (agbr >> 8) & 0xff;
                                    cumB += (agbr >> 16) & 0xff;

                                    pixels++;
                                }
                            }
                        }

                        if (pixels == 0)
                            return -1;

                        cumR /= pixels;
                        cumG /= pixels;
                        cumB /= pixels;

                        return ((0xFF) << 24) | ((cumR & 0xFF) << 16) | ((cumG & 0xFF) << 8) | (cumB & 0xFF);
                    }
                }
            }
        }
        return -1;
    }

    @Override
    @Nullable
    public Sprite[] getSprites(FluidVariant fluidVariant)
    {
        // Use the fluid render handler by default.
        FluidRenderHandler fluidRenderHandler = FluidRenderHandlerRegistry.INSTANCE.get(fluidVariant.getFluid());

        if (fluidRenderHandler != null)
        {
            return fluidRenderHandler.getFluidSprites(null, null, fluidVariant.getFluid().getDefaultState());
        }
        else
        {
            return null;
        }
    }
}
