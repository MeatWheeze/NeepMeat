package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.MeatWeapons;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.network.MWAttackC2SPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Matrix4f;

import java.util.List;
import java.util.UUID;

public interface MeatgunModule
{
    UUID getUuid();

    List<ModuleSlot> getChildren();

    Type<? extends MeatgunModule> getType();

    default void receivePacket(PacketByteBuf buf) {}

    default void tick(PlayerEntity player)
    {
        getChildren().forEach(s -> s.get().tick(player));
    }

    default void trigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        getChildren().forEach(c -> c.get().trigger(world, player, stack, id, pitch, yaw, handType));
    }

    default void release(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        getChildren().forEach(c -> c.get().release(world, player, stack, id, pitch, yaw, handType));
    }

    default void tickTrigger(World world, PlayerEntity player, ItemStack stack, int id, double pitch, double yaw, MWAttackC2SPacket.HandType handType)
    {
        getChildren().forEach(c -> c.get().tickTrigger(world, player, stack, id, pitch, yaw, handType));
    }

    void setTransform(Matrix4f transform);

    NbtCompound writeNbt(NbtCompound nbt);

    void readNbt(NbtCompound nbt);

    static NbtCompound toNbt(MeatgunModule module)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", module.getType().getId().toString());
        module.writeNbt(nbt);
        return nbt;
    }

    static Type<?> readType(NbtCompound nbt)
    {
        Identifier id = new Identifier(nbt.getString("id"));
        return MeatgunModules.REGISTRY.get(id);
    }

    MeatgunModule DEFAULT = new MeatgunModule()
    {
        @Override
        public UUID getUuid()
        {
            return UUID.fromString("");
        }

        @Override
        public List<ModuleSlot> getChildren()
        {
            return List.of();
        }

        @Override
        public Type<? extends MeatgunModule> getType()
        {
            return DEFAULT_TYPE;
        }

        @Override
        public void setTransform(Matrix4f transform)
        {

        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt)
        {
            return nbt;
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {

        }

//        @Override
//        public EnumSet<ChildProperties> getChildProperties()
//        {
//             TODO
//            return null;
//        }
    };

    Type<?> DEFAULT_TYPE = new MeatgunModule.Type<>(new Identifier(MeatWeapons.NAMESPACE, "default"), (l, p) -> DEFAULT, (l, n) -> DEFAULT);



    @FunctionalInterface
    interface Factory<T extends MeatgunModule>
    {
        T create(MeatgunComponent.Listener listener, MeatgunModule parent);
    }

    @FunctionalInterface
    interface NbtFactory<T extends MeatgunModule>
    {
        T create(MeatgunComponent.Listener listener, NbtCompound nbt);
    }

    class Type<T extends MeatgunModule>
    {
        private final Identifier id;
        private final Factory<T> factory;

        private final NbtFactory<T> nbtFactory;

        public Type(Identifier id, Factory<T> factory, NbtFactory<T> nbtFactory)
        {
            this.id = id;
            this.factory = factory;
            this.nbtFactory = nbtFactory;
        }

        public Identifier getId()
        {
            return id;
        }

        public T create(MeatgunComponent.Listener listener, MeatgunModule parent)
        {
            return factory.create(listener, parent);
        }

        public T create(MeatgunComponent.Listener listener, NbtCompound nbt)
        {
            return nbtFactory.create(listener, nbt);
        }
    }

    enum ChildProperties
    {
        AUXILIARY,


    }

    enum ParentProperties
    {

    }
}
