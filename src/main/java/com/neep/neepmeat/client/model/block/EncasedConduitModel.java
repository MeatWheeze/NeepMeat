package com.neep.neepmeat.client.model.block;

import com.mojang.datafixers.util.Pair;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.block.EncasedBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(value= EnvType.CLIENT)
public class EncasedConduitModel implements UnbakedModel, BakedModel, FabricBakedModel
{
    private final Identifier frameId = new Identifier(NeepMeat.NAMESPACE, "block/encased_frame");
    private final SpriteIdentifier frameTextureId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(NeepMeat.NAMESPACE, "block/encased_frame"));
    private final SpriteIdentifier particleSpriteId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(NeepMeat.NAMESPACE, "block/vascular_conduit/pipe_centre"));
    private Sprite particleSprite;
    private BakedModel frame;

    /* UnbakedModel */
    @Override
    public Collection<Identifier> getModelDependencies()
    {
        return List.of(frameId);
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences)
    {
        return List.of(particleSpriteId, frameTextureId);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings settings, Identifier modelId)
    {
        particleSprite = textureGetter.apply(particleSpriteId);

        UnbakedModel m = loader.getOrLoadModel(frameId);
        this.frame = m.bake(loader, textureGetter, new ModelVariant(frameId, settings.getRotation(), settings.isUvLocked(), 1), frameId);
        return this;
    }

    /* FabricBakedModel */
    @Override
    public boolean isVanillaAdapter()
    {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context)
    {
        if (blockView.getBlockEntity(pos) instanceof EncasedBlockEntity entity)
        {
            BlockState camoState = entity.getCamoState();
            if (!camoState.isAir() && !camoState.isOf(state.getBlock()))
            {
                BakedModel camoModel = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(camoState);
                if (camoModel instanceof FabricBakedModel fabricBakedModel)
                {
                    fabricBakedModel.emitBlockQuads(blockView, camoState, pos, randomSupplier, context);
                }
            }
            else
            {
                context.bakedModelConsumer().accept(frame);
            }
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context)
    {

    }

    /* BakedModel */
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean hasDepth()
    {
        return false;
    }

    @Override
    public boolean isSideLit()
    {
        return false;
    }

    @Override
    public boolean isBuiltin()
    {
        return false;
    }

    @Override
    public Sprite getParticleSprite()
    {
        return particleSprite;
    }

    @Override
    public ModelTransformation getTransformation()
    {
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides()
    {
        return ModelOverrideList.EMPTY;
    }
}
