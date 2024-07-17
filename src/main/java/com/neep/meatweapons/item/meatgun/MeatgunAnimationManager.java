package com.neep.meatweapons.item.meatgun;

import com.neep.meatweapons.client.meatgun.animation.MeatgunAnimation;
import com.neep.meatweapons.component.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.collections4.map.AbstractReferenceMap;
import org.apache.commons.collections4.map.ReferenceMap;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class MeatgunAnimationManager
{
    private static final Map<UUID, MeatgunAnimationManager> INSTANCES = new ReferenceMap<>(AbstractReferenceMap.ReferenceStrength.HARD, AbstractReferenceMap.ReferenceStrength.SOFT, true);

    public static MeatgunAnimationManager getOrCreate(UUID uuid, Meatgun meatgun)
    {
        return INSTANCES.computeIfAbsent(uuid, u -> (MeatgunAnimationManager) meatgun.createAnimationManager().get());
    }

    private final MeatgunAnimation idle;
    private MeatgunAnimation activeAnimation = MeatgunAnimation.EMPTY;

    private final Map<String, MeatgunAnimation> animations = new HashMap<>();

    public MeatgunAnimationManager(MeatgunAnimation idle)
    {
        this.idle = idle;
        this.activeAnimation = idle;
        this.activeAnimation.start(null);
    }

    public void queue(String name, @Nullable PacketByteBuf buf)
    {
        @Nullable MeatgunAnimation animation = animations.get(name);
        if (animation != null)
            queue(animation, buf);
    }

    public void queue(MeatgunAnimation animation, @Nullable PacketByteBuf buf)
    {
        if (activeAnimation.canStop(animation))
        {
            activeAnimation = animation;
            activeAnimation.start(buf);
        }
    }

    public void tick(MeatgunComponent component)
    {
        if (activeAnimation.finished())
        {
            activeAnimation = idle;
            activeAnimation.start(null);
        }

        activeAnimation.tick(component);
    }

    public MeatgunAnimation getActive()
    {
        return activeAnimation;
    }

    public MeatgunAnimationManager add(String name, MeatgunAnimation animation)
    {
        animations.put(name, animation);
        return this;
    }
}
