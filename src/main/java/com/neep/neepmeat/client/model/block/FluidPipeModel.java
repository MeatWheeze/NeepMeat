package com.neep.neepmeat.client.model.block;

import com.mojang.datafixers.util.Pair;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.transport.FluidTransport;
import com.neep.neepmeat.transport.api.pipe.IFluidPipe;
import com.neep.neepmeat.transport.block.fluid_transport.FluidPipeBlock;
import com.neep.neepmeat.transport.fluid_network.PipeConnectionType;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(value= EnvType.CLIENT)
public class FluidPipeModel implements UnbakedModel, BakedModel, FabricBakedModel
{
    private static final Identifier SIDE_ID = new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_side_up");
    private static final Identifier STRAIGHT_ID = new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_straight_up");
    private final BlockPos.Mutable mutable = new BlockPos.Mutable();

//    private static final Identifier[] CONNECTOR_IDS = new Identifier[]
//            {
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_side"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_side_alt"),
//            };
//
//    private static final Identifier[] STRAIGHT_IDS = new Identifier[]
//            {
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_centre"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_centre_ns"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_centre_ew"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_side"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_side_alt"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_straight"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_straight_alt"),
//            };
//
//    private static final Identifier[] CENTRE_IDS = new Identifier[]
//            {
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_centre"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_centre_ns"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_centre_ew"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_side"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_side_alt"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_straight"),
//                    new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_straight_alt"),
//            };

    private static final SpriteIdentifier PARTICLE_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/pipe_centre"));
    private static Sprite PARTICLE_SPRITE;

    private final Map<BlockState, BitSet> stateCache = new Object2ObjectOpenCustomHashMap<>(Util.identityHashStrategy());

    private final Triple<BakedModel, Float, Float>[] straight = (Triple<BakedModel, Float, Float>[]) Array.newInstance(Triple.class, 6);
    private final Triple<BakedModel, Float, Float>[] connectors = (Triple<BakedModel, Float, Float>[]) Array.newInstance(Triple.class, 6);

    /* UnbakedModel */
    @Override
    public Collection<Identifier> getModelDependencies()
    {
        return List.of(SIDE_ID, STRAIGHT_ID);
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences)
    {
        return List.of(PARTICLE_SPRITE_ID, new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(NeepMeat.NAMESPACE, "block/rusty_pipe/rusty_pipe_straight")));
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId)
    {
        PARTICLE_SPRITE = textureGetter.apply(PARTICLE_SPRITE_ID);

//        Arrays.stream(CENTRE_IDS).forEach(id ->
//        {
//            BakedModel bakedPart = loader.getOrLoadModel(id).bake(loader, textureGetter, rotationContainer, modelId);
//            if (bakedPart != null) parts.add(bakedPart);
//        });

        try
        {
            addPart(connectors, Direction.NORTH, SIDE_ID, 0f, 0f, loader, textureGetter, modelId);
            addPart(connectors, Direction.SOUTH, SIDE_ID, 0f, 0f, loader, textureGetter, modelId);
            addPart(connectors, Direction.EAST, SIDE_ID, 270f, 0f, loader, textureGetter, modelId);
            addPart(connectors, Direction.WEST, SIDE_ID, 270f, 0f, loader, textureGetter, modelId);
            addPart(connectors, Direction.UP, SIDE_ID, 270f, 270f, loader, textureGetter, modelId);
            addPart(connectors, Direction.DOWN, SIDE_ID, 270f, 270f, loader, textureGetter, modelId);

            addPart(straight, Direction.NORTH, STRAIGHT_ID, 0f, 0f, loader, textureGetter, modelId);
            addPart(straight, Direction.SOUTH, STRAIGHT_ID, 0f, 0f, loader, textureGetter, modelId);
            addPart(straight, Direction.EAST, STRAIGHT_ID, 270f, 0f, loader, textureGetter, modelId);
            addPart(straight, Direction.WEST, STRAIGHT_ID, 270f, 0f, loader, textureGetter, modelId);
            addPart(straight, Direction.UP, STRAIGHT_ID, 270f, 270f, loader, textureGetter, modelId);
            addPart(straight, Direction.DOWN, STRAIGHT_ID, 270f, 270f, loader, textureGetter, modelId);

            UnbakedModel unbaked = loader.getOrLoadModel(SIDE_ID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }

    protected void addPart(Triple<BakedModel, Float, Float>[] parts, Direction face, Identifier id, float x, float y, ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, Identifier modelId)
    {
        UnbakedModel unbaked = loader.getOrLoadModel(id);
        ModelVariant settings = new ModelVariant(id, new AffineTransformation(null, null, null, face.getRotationQuaternion()), false, 1);
        parts[face.getId()] = Triple.of(unbaked.bake(loader, textureGetter, settings, modelId), x, y);
    }

    /* FabricBakedModel */
    @Override
    public boolean isVanillaAdapter() {return false;}

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context)
    {
//        ((FabricBakedModel) connectors[Direction.NORTH.getId()].getLeft()).emitBlockQuads(blockView, state, pos, randomSupplier, context);
        for (Direction direction : Direction.values())
        {
            mutable.set(pos);
            EnumProperty<PipeConnectionType> property = FluidPipeBlock.DIR_TO_CONNECTION.get(direction);
            EnumProperty<PipeConnectionType> backProperty = FluidPipeBlock.DIR_TO_CONNECTION.get(direction.getOpposite());
            boolean forward = IFluidPipe.isConnectedIn(blockView, pos, state, direction);
            if (!forward) continue;

            BlockState offsetState = blockView.getBlockState(pos.offset(direction));
            if (!IFluidPipe.isConnectedIn(blockView, pos, state, direction.getOpposite()) || !(offsetState.getBlock() instanceof IFluidPipe) || !IFluidPipe.isConnectedIn(blockView, pos, offsetState, direction))
            {
                ((FabricBakedModel) connectors[direction.getId()].getLeft()).emitBlockQuads(blockView, state, pos, randomSupplier, context);
            }
            else if (IFluidPipe.isConnectedIn(blockView, pos, offsetState, direction))
            {
                ((FabricBakedModel) straight[direction.getId()].getLeft()).emitBlockQuads(blockView, state, pos, randomSupplier, context);
            }
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context)
    {

    }

    boolean renderConnector(Direction face, BlockState state, BlockState offsetState)
    {
        EnumProperty<PipeConnectionType> connection = FluidPipeBlock.DIR_TO_CONNECTION.get(face);
        return state.get(connection).isConnected() && (!offsetState.isOf(FluidTransport.PIPE) || !offsetState.get(connection).isConnected());
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
        return false;
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
        return PARTICLE_SPRITE;
    }

    @Override
    public ModelTransformation getTransformation()
    {
        return null;
    }

    @Override
    public ModelOverrideList getOverrides()
    {
        return null;
    }

//    @Environment(value= EnvType.CLIENT)
//    public static class Builder
//    {
//        private final List<Pair<Predicate<BlockState>, BakedModel>> components = Lists.newArrayList();
//
//        public void addComponent(Predicate<BlockState> predicate, BakedModel model)
//        {
//            this.components.add(Pair.of(predicate, model));
//        }
//
//        public BakedModel build()
//        {
//            return new MultipartBakedModel(this.components);
//        }
//    }
}
