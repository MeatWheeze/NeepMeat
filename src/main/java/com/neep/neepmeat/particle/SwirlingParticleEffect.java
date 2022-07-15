package com.neep.neepmeat.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;

public class SwirlingParticleEffect implements ParticleEffect
{
    public static final ParticleEffect.Factory<SwirlingParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>()
    {
        @Override
        public SwirlingParticleEffect read(ParticleType<SwirlingParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException
        {
            stringReader.expect(' ');
            BlockState state = (new BlockArgumentParser(stringReader, false)).parse(false).getBlockState();
            stringReader.expect(' ');
            double radius = stringReader.readDouble();
            return new SwirlingParticleEffect(particleType, state, radius);
        }

        @Override
        public SwirlingParticleEffect read(ParticleType<SwirlingParticleEffect> particleType, PacketByteBuf packetByteBuf)
        {
            return new SwirlingParticleEffect(particleType, Block.STATE_IDS.get(packetByteBuf.readVarInt()), packetByteBuf.readDouble());
        }
    };

    private final ParticleType<SwirlingParticleEffect> type;
    private final BlockState blockState;
    private final double radius;

    public SwirlingParticleEffect(ParticleType<SwirlingParticleEffect> type, BlockState state, double radius)
    {
        this.type = type;
        this.blockState = state;
        this.radius = radius;
    }

    public static Codec<SwirlingParticleEffect> createCodec(ParticleType<SwirlingParticleEffect> type)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                        (BlockState.CODEC.fieldOf("blockstate")).forGetter(effect -> effect.blockState),
                        (Codec.DOUBLE.fieldOf("radius")).forGetter(effect -> effect.radius)
                        ).apply(instance, (blockState1, aDouble) -> new SwirlingParticleEffect(type, blockState1, aDouble)));

//        return BlockState.CODEC.xmap((state) -> new SwirlingParticleEffect(type, state, radius), effect -> effect.blockState);
//        return BlockState.createCodec
    }

    @Override
    public ParticleType<?> getType()
    {
        return type;
    }

    @Override
    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(Block.STATE_IDS.getRawId(this.blockState));
    }

    @Override
    public String asString()
    {
        return Registry.PARTICLE_TYPE.getId(this.getType()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
    }

    public BlockState getBlockState()
    {
        return blockState;
    }
}
