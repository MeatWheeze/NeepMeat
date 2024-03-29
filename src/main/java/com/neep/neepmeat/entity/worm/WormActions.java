package com.neep.neepmeat.entity.worm;

import com.neep.neepmeat.util.RandomMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class WormActions
{
    protected static RandomMap<Identifier, Entry> MAP = new RandomMap<>();

    public static void put(Identifier id, WormActionFactory factory, float weight)
    {
        Entry entry = new Entry(weight, factory);
        MAP.put(id, entry, weight);

        // Make corresponding animation available
//        AnimationBuilder animationBuilder = new AnimationBuilder().addAnimation(animation);
//        ANIMATION_MAP.put(id.toString(), animationBuilder);
    }
    public static Entry random()
    {
        return MAP.next();
    }

    public static void init()
    {
        Entry emptyEntry = new Entry(1, WormAction.EmptyAction::new);
        MAP.getById().defaultReturnValue(emptyEntry);
//        ANIMATION_MAP.defaultReturnValue(IDLE);

//        put(IdleWormAction.ID, IdleWormAction::new, 1);
//        put(FullSwingWormAction.ID, FullSwingWormAction::new, 1);
        put(WormBiteGoal.ID, WormBiteGoal::new, 1);
    }

    public static NbtCompound toNbt(WormAction action)
    {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id", action.toString());
        action.writeNbt(nbt);
        return nbt;
    }

    public static WormAction fromNbt(NbtCompound nbt, WormEntity worm)
    {
        Identifier id = Identifier.tryParse(nbt.getString("id"));
        Entry entry = MAP.get(id);
        WormAction action = entry.create(worm);
        action.readNbt(nbt);
        return action;
    }

//    protected static Object2ObjectMap<String, AnimationBuilder> ANIMATION_MAP = new Object2ObjectOpenHashMap<>();
//    protected static final AnimationBuilder IDLE = new AnimationBuilder().addAnimation("animation.god_worm.idle");

//    public static AnimationBuilder getAnimation(String actionId)
//    {
//        return ANIMATION_MAP.getOrDefault(actionId, IDLE);
//    }

    public record Entry(float weight, WormActionFactory factory)
    {
        public WormAction create(WormEntity entity)
        {
            return factory.create(entity);
        }
    }

    @FunctionalInterface
    public interface WormActionFactory
    {
        WormAction create(WormEntity entity);
    }
}
