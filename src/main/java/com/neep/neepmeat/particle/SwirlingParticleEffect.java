package com.neep.neepmeat.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;

public class SwirlingParticleEffect implements ParticleEffect
{
    public static final ParticleEffect.Factory<SwirlingParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>()
    {
        @Override
        public SwirlingParticleEffect read(ParticleType<SwirlingParticleEffect> particleType, StringReader stringReader) throws CommandSyntaxException
        {
            stringReader.expect(' ');
            BlockState state = (BlockArgumentParser.block(Registries.BLOCK.getReadOnlyWrapper(), stringReader, false)).blockState();
            stringReader.expect(' ');
            double radius = stringReader.readDouble();
            stringReader.expect(' ');
            double speed = stringReader.readDouble();
            return new SwirlingParticleEffect(particleType, state, radius, speed);
        }

        @Override
        public SwirlingParticleEffect read(ParticleType<SwirlingParticleEffect> particleType, PacketByteBuf packetByteBuf)
        {
            return new SwirlingParticleEffect(particleType, Block.STATE_IDS.get(packetByteBuf.readVarInt()), packetByteBuf.readDouble(), packetByteBuf.readDouble());
        }
    };

    private final ParticleType<SwirlingParticleEffect> type;
    private final BlockState blockState;
    public final double radius;
    public final double speed;

    public SwirlingParticleEffect(ParticleType<SwirlingParticleEffect> type, BlockState state, double radius, double angularSpeed)
    {
        this.type = type;
        this.blockState = state;
        this.radius = radius;
        this.speed = angularSpeed;
    }

    public static Codec<SwirlingParticleEffect> createCodec(ParticleType<SwirlingParticleEffect> type)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                        (BlockState.CODEC.fieldOf("blockstate")).forGetter(effect -> effect.blockState),
                        (Codec.DOUBLE.fieldOf("radius")).forGetter(effect -> effect.radius),
                        (Codec.DOUBLE.fieldOf("speed")).forGetter(effect -> effect.speed)
                        ).apply(instance, (state, radius, speed) -> new SwirlingParticleEffect(type, state, radius, speed)));

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
        return Registries.PARTICLE_TYPE.getId(this.getType()) + " " + BlockArgumentParser.stringifyBlockState(this.blockState);
    }

    public BlockState getBlockState()
    {
        return blockState;
    }
}
