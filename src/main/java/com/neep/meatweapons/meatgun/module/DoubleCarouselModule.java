package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.item.meatgun.MeatgunComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.util.List;

public class DoubleCarouselModule extends AbstractMeatgunModule
{
//    static Codec<DoubleCarouselModule> CODEC = Codec.of(
//            new Encoder<>()
//            {
//                @Override
//                public <T> DataResult<T> encode(DoubleCarouselModule input, DynamicOps<T> ops, T prefix)
//                {
//                    return null;
//                }
//            },
//            new Decoder<>()
//            {
//                @Override
//                public <T> DataResult<Pair<DoubleCarouselModule, T>> decode(DynamicOps<T> ops, T input)
//                {
//                    var list = ops.getList(input);
//                    return Pair.of();
//                }
//            }
//    );

//    static Codec<DoubleCarouselModule> CODEC = RecordCodecBuilder.create(instance ->
//    {
//        instance.group(
//        )
//    })

    private final ModuleSlot upSlot;
    private final ModuleSlot downSlot;
    private final ModuleSlot auxSlot;

    public DoubleCarouselModule(MeatgunComponent.Listener listener)
    {
        super(listener);
        auxSlot = new SimpleModuleSlot(this.listener, new Matrix4f().rotateY(MathHelper.PI).translate(0, 5 / 16f, 0 / 16f));
        downSlot = new SimpleModuleSlot(this.listener, new Matrix4f().rotateZ(MathHelper.PI).translate(0, 4 / 16f, -3 / 16f));
        upSlot = new SimpleModuleSlot(this.listener, new Matrix4f().translate(0, 4 / 16f, -3 / 16f));
        setSlots(List.of(upSlot, downSlot, auxSlot));
    }

    public DoubleCarouselModule(MeatgunComponent.Listener listener, NbtCompound nbt)
    {
        this(listener);
    }

    @Override
    public Type<? extends MeatgunModule> getType()
    {
        return MeatgunModules.DOUBLE_CAROUSEL;
    }

}
