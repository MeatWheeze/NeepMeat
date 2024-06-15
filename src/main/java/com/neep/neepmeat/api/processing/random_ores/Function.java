package com.neep.neepmeat.api.processing.random_ores;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;

enum Function
{
    ADD,
    MUL,
    ;

    static final Codec<Function> CODEC = new PrimitiveCodec<>()
    {
        @Override
        public <T> DataResult<Function> read(DynamicOps<T> ops, T input)
        {
            return ops.getStringValue(input).map(s -> Function.valueOf(s.toUpperCase()));
        }

        @Override
        public <T> T write(DynamicOps<T> ops, Function value)
        {
            return ops.createString(value.toString().toLowerCase());
        }
    };
}
