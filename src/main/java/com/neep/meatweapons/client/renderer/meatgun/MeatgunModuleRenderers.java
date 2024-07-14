package com.neep.meatweapons.client.renderer.meatgun;

import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.meatgun.module.MeatgunModules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class MeatgunModuleRenderers
{
    private static final Map<MeatgunModule.Type<?>, MeatgunModuleRenderer.Factory<?>> RENDERER_FACTORIES = new HashMap<>();
    private static final Map<MeatgunModule.Type<? extends MeatgunModule>, MeatgunModuleRenderer<?>> RENDERERS = new HashMap<>();

    public static <T extends MeatgunModule> void register(MeatgunModule.Type<T> type, MeatgunModuleRenderer.Factory<T> factory)
    {
        RENDERER_FACTORIES.put(type, factory);
    }

    public static <T extends MeatgunModule> MeatgunModuleRenderer<T> get(T module)
    {
        return (MeatgunModuleRenderer<T>)
                RENDERERS.computeIfAbsent(module.getType(), id -> RENDERER_FACTORIES.get(module.getType()).create(MinecraftClient.getInstance()));
    }

    public static void init()
    {
        register(MeatgunModules.BASE_PISTOL, BasePistolModuleRenderer::new);
        register(MeatgunModules.BASE_STAFF, BaseStaffModuleRenderer::new);
        register(MeatgunModules.PISTOL, PistolModuleRenderer::new);
        register(MeatgunModules.CHUGGER, ChuggerModuleRenderer::new);
        register(MeatgunModules.BOSHER, BosherModuleRenderer::new);
        register(MeatgunModules.LONG_BOI, LongBoiModuleRenderer::new);
        register(MeatgunModules.GRENADE_LAUNCHER, GrenadeLauncherModuleRenderer::new);
        register(MeatgunModules.BLOODTHROWER, BloodthrowerModuleRenderer::new);
        register(MeatgunModules.TRIPLE_CAROUSEL, TripleCarouselModuleRenderer::new);
        register(MeatgunModules.DOUBLE_CAROUSEL, DoubleCarouselModuleRenderer::new);
        register(MeatgunModules.UNDERBARREL, UnderbarrelModuleRenderer::new);
        register(MeatgunModules.BATTERY, BatteryModuleRenderer::new);
    }
}
