package com.neep.neepmeat.client.model.block;

import com.neep.neepmeat.NeepMeat;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class SlopeTest implements UnbakedModel, BakedModel, FabricBakedModel
{

    private static final Identifier DEFAULT_BLOCK_MODEL = new Identifier("minecraft:block/block");
    private ModelTransformation transformation;

    private final SpriteIdentifier[] SPRITE_IDS =
            {
            new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(NeepMeat.NAMESPACE, "block/blue_metal_scaffold_side")),
            new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(NeepMeat.NAMESPACE, "block/scaffold_top")),
    };

    private final Sprite[] SPRITES = new Sprite[2];

    private final Mesh[] SIDES = new Mesh[6];
    private final Mesh[] SIDES_INV = new Mesh[6];
    private Mesh mesh;

    public SlopeTest()
    {
//        SPRITE_IDS[0] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, sideTexture);
//        SPRITE_IDS[1] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, topTexture);
    }

    public SlopeTest(String namespace, String registryName)
    {
//        SPRITE_IDS[0] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(namespace, registryName + "_side"));
//        SPRITE_IDS[0] = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(namespace, registryName + "_top"));
    }

    @Override
    public Collection<Identifier> getModelDependencies()
    {
        return Arrays.asList(DEFAULT_BLOCK_MODEL);
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> modelLoader)
    {

    }

//    @Override
//    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences)
//    {
//        return Arrays.asList(SPRITE_IDS);
//    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId)
    {
        // Get the sprites
        for(int i = 0; i < 1; ++i) {
            SPRITES[i] = textureGetter.apply(SPRITE_IDS[i]);
        }
        // Build the mesh using the Renderer API
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();

        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        float left = 0.8f;
        float bottom = 0;
        float right = 1;
        float top = 1;
        float depth = 0.5f;

        // Top face
        emitter.pos(2, 0, 1, bottom); //nw
        emitter.pos(3, 0f, 0f, top); //sw
        emitter.pos(0, right, 0, top); //se
        emitter.pos(1, right, 1f, bottom); //ne
        emitter.nominalFace(Direction.UP);

        emitter.spriteBake(0, SPRITES[0], MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        // North face
        emitter.pos(2, 0, 0, bottom); //nw
        emitter.pos(1, 1, 0f, bottom); //ne
        emitter.pos(3, 0f, 1f, bottom); //sw
        emitter.pos(0, 1, 1, bottom); //se
        emitter.nominalFace(Direction.NORTH);

        emitter.spriteBake(0, SPRITES[0], MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        // East face
        emitter.pos(2, 1, 0, 0); //nw
        emitter.pos(1, 1, 0, 1); //ne
        emitter.pos(3, 1, 1, 0); //sw
        emitter.pos(0, 1, 0, 1); //se
        emitter.nominalFace(Direction.EAST);

        emitter.spriteBake(0, SPRITES[0], MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        // West Face
        emitter.pos(0, 0, 0, 0);
        emitter.pos(1, 0, 0, 1);
        emitter.pos(2, 0, 1, 0);
        emitter.pos(3, 0, 0, 1);
        emitter.nominalFace(Direction.WEST);

        emitter.spriteBake(0, SPRITES[0], MutableQuadView.BAKE_LOCK_UV);
        emitter.spriteColor(0, -1, -1, -1, -1);
        emitter.emit();

        mesh = builder.build();

        JsonUnbakedModel defaultBlockModel = (JsonUnbakedModel) baker.getOrLoadModel(DEFAULT_BLOCK_MODEL);
        transformation = defaultBlockModel.getTransformations();

        return this;
    }

    @Override
    public boolean isVanillaAdapter()
    {
        return false;
    }

//    @Override
//    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context)
//    {
//        Direction direction = state.get(BaseStairsBlock.FACING);
////        context.pushTransform(Vec3f.POSITIVE_Y.getDegreesQuaternion(direction.asRotation()));
//        context.pushTransform((quad -> {
//            for (int i = 0; i < 4; ++i)
//            {
//                Vec3f vert1 = quad.copyPos(i, null);
//                Vec3f vert = quad.copyPos(i, null);
//                vert.add(-0.5f, 0, -0.5f);
//                vert.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(-direction.asRotation() - 180f));
////                vert1.rotate(Vec3f.POSITIVE_Y.getDegreesQuaternion(0f));
//                vert.add(0.5f, 0, 0.5f);
//                quad.pos(i, vert);
//            }
//            return true;
//        }));
//        context.meshConsumer().accept(mesh);
//        context.popTransform();
//    }

//    @Override
//    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context)
//    {
//        for (Direction direction : Direction.values())
//        {
//            context.meshConsumer().accept(SIDES[direction.getId()]);
//        }
//    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, net.minecraft.util.math.random.Random random)
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
