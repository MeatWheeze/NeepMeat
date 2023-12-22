package com.neep.neepmeat.client.model.block;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class ScaffoldTopModel implements UnbakedModel, BakedModel, FabricBakedModel
{

    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");
    private ModelTransformation transformation;

    private final SpriteIdentifier[] SPRITE_IDS = new SpriteIdentifier[2];
//            {
//            new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(NeepMeat.NAMESPACE, "block/scaffold_side")),
//            new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(NeepMeat.NAMESPACE, "block/scaffold_top")),
//    };

    private final Sprite[] SPRITES = new Sprite[2];

    private final Mesh[] SIDES = new Mesh[6];
    private final Mesh[] SIDES_INV = new Mesh[6];

    private Mesh outerMesh;
    private Mesh innerMesh;
    private final Block block;

    public ScaffoldTopModel(Identifier sideTexture, Identifier topTexture, Block block)
    {
        SPRITE_IDS[0] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, sideTexture);
        SPRITE_IDS[1] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, topTexture);
        this.block = block;
    }

    public ScaffoldTopModel(String namespace, String registryName, Block block)
    {
        SPRITE_IDS[0] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(namespace, registryName + "_side"));
        SPRITE_IDS[0] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(namespace, registryName + "_top"));
        this.block = block;
    }

    @Override
    public Collection<Identifier> getModelDependencies()
    {
        return Arrays.asList(DEFAULT_BLOCK_MODEL);
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences)
    {
        return Arrays.asList(SPRITE_IDS);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId)
    {
        // Get the sprites
        for(int i = 0; i < 2; ++i) {
            SPRITES[i] = textureGetter.apply(SPRITE_IDS[i]);
        }
        // Build the mesh using the Renderer API
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        // Create outer faces
        for(Direction direction : Direction.values())
        {

            int spriteIdx = direction == Direction.UP || direction == Direction.DOWN ? 1 : 0;

            emitter.square(direction, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f);
            emitter.spriteBake(0, SPRITES[spriteIdx], MutableQuadView.BAKE_LOCK_UV);
            emitter.spriteColor(0, -1, -1, -1, -1);
            emitter.emit();

        }
        outerMesh = builder.build();

        // Create inner faces
        for(Direction direction : Direction.values())
        {
            int spriteIdx = direction == Direction.UP || direction == Direction.DOWN ? 1 : 0;

            emitter.square(direction.getOpposite(), 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
            emitter.nominalFace(direction);
            emitter.spriteBake(0, SPRITES[spriteIdx], MutableQuadView.BAKE_LOCK_UV);
            emitter.spriteColor(0, -1, -1, -1, -1);
            emitter.emit();

        }
        innerMesh = builder.build();

        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) loader.getOrLoadModel(DEFAULT_BLOCK_MODEL);
        transformation = defaultBlockModel.getTransformations();

        return this;
    }

    @Override
    public boolean isVanillaAdapter()
    {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context)
    {
        // Janky scaffolding rendering.
//        for (Direction direction : Direction.values())
//        {
//            if (!blockView.getBlockState(pos.offset(direction)).isOf(BlockInitialiser.SCAFFOLD_PLATFORM))
//            {
//                context.pushTransform(quad ->
//                {
//                    for (int i = 0; i < 4; ++i)
//                    {
//                        Vec3f vert = quad.copyPos(i, null);
//                        vert.add(-0.5f, -0.5f, -0.5f);
//                        vert.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(direction.asRotation()));
//                        vert.add(0.5f, 0.5f, 0.5f);
//                    }
//                    return true;
//                });
//                context.meshConsumer().accept(SIDES[0]);
//                context.popTransform();
//            }

//        context.pushTransform(quad ->
//        {
//            Direction face = quad.nominalFace();
//            if (blockView.getBlockState(pos.offset(face)).isOf(BlockInitialiser.SCAFFOLD_PLATFORM))
//            {
//                return false;
//            }
//            for (int i = 0; i < 4; ++i)
//            {
//                Vec3f vert = quad.copyPos(i, null);
//                vert.add(-0.5f, -0.5f, -0.5f);
//                vert.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(face.asRotation()));
//                vert.add(0.5f, 0.5f, 0.5f);
//            }
//            return true;
//        });

        context.pushTransform(quad -> getFaces(quad, blockView, pos, block));
        context.meshConsumer().accept(outerMesh);
        context.meshConsumer().accept(innerMesh);
        context.popTransform();

    }

    public static boolean getFaces(MutableQuadView quad, BlockRenderView blockView, BlockPos pos, Block block)
    {
        Direction face = quad.nominalFace();
        BlockPos newPos = pos.offset(face);
        BlockState newState = blockView.getBlockState(newPos);
        return !(newState.isOf(block) || newState.isSideSolidFullSquare(blockView, newPos, face.getOpposite()));
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context)
    {
        context.meshConsumer().accept(outerMesh);
    }

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
        return true;
    }

    @Override
    public boolean isBuiltin()
    {
        return false;
    }

    @Override
    public Sprite getParticleSprite()
    {
        return SPRITES[0];
    }

    @Override
    public ModelTransformation getTransformation()
    {
        return transformation;
    }

    @Override
    public ModelOverrideList getOverrides()
    {
        return ModelOverrideList.EMPTY;
    }
}
