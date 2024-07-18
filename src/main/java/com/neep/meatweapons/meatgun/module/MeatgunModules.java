package com.neep.meatweapons.meatgun.module;

import com.neep.meatweapons.MeatWeapons;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class MeatgunModules
{
    public static RegistryKey<Registry<MeatgunModule.Type<? extends MeatgunModule>>> REGISTRY_KEY = RegistryKey.ofRegistry(new Identifier(MeatWeapons.NAMESPACE, "meatgun_module"));
    public static final DefaultedRegistry<MeatgunModule.Type<? extends MeatgunModule>> REGISTRY = FabricRegistryBuilder.createDefaulted(REGISTRY_KEY, MeatgunModule.DEFAULT_TYPE.getId()).buildAndRegister();

    public static final MeatgunModule.Type<BasePistolModule> BASE_PISTOL = register(new Identifier(MeatWeapons.NAMESPACE, "base_pistol"), 0, (l, p) -> new BasePistolModule(l), BasePistolModule::fromNbt);
    public static final MeatgunModule.Type<BaseStaffModule> BASE_STAFF = register(new Identifier(MeatWeapons.NAMESPACE, "base_staff"), 0, (l, p) -> new BaseStaffModule(l), BaseStaffModule::fromNbt);
    public static final MeatgunModule.Type<PistolModule> PISTOL = register(new Identifier(MeatWeapons.NAMESPACE, "pistol"), 4, (l, p) -> new PistolModule(l), PistolModule::new);
    public static final MeatgunModule.Type<ChuggerModule> CHUGGER = register(new Identifier(MeatWeapons.NAMESPACE, "chugger"), 6, (l, p) -> new ChuggerModule(l), ChuggerModule::new);
    public static final MeatgunModule.Type<BosherModule> BOSHER = register(new Identifier(MeatWeapons.NAMESPACE, "bosher"), 8, (l, p) -> new BosherModule(l), BosherModule::new);
    public static final MeatgunModule.Type<LongBoiModule> LONG_BOI = register(new Identifier(MeatWeapons.NAMESPACE, "long_boi"), 10, (l, p) -> new LongBoiModule(l), LongBoiModule::new);
    public static final MeatgunModule.Type<GrenadeLauncherModule> GRENADE_LAUNCHER = register(new Identifier(MeatWeapons.NAMESPACE, "grenade_launcher"), 6, (l, p) -> new GrenadeLauncherModule(l), GrenadeLauncherModule::new);
    public static final MeatgunModule.Type<BloodthrowerModule> BLOODTHROWER = register(new Identifier(MeatWeapons.NAMESPACE, "bloodthrower"), 8, (l, p) -> new BloodthrowerModule(l), BloodthrowerModule::new);
    public static final MeatgunModule.Type<TripleCarouselModule> TRIPLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "triple_carousel"), 2, (l, p) -> new TripleCarouselModule(l), TripleCarouselModule::new);
    public static final MeatgunModule.Type<DoubleCarouselModule> DOUBLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "double_carousel"), 2, (l, p) -> new DoubleCarouselModule(l), DoubleCarouselModule::new);
    public static final MeatgunModule.Type<UnderbarrelModule> UNDERBARREL = register(new Identifier(MeatWeapons.NAMESPACE, "underbarrel"), 2, (l, p) -> new UnderbarrelModule(l), UnderbarrelModule::new);

    public static final MeatgunModule.Type<BatteryModule> BATTERY = register(new Identifier(MeatWeapons.NAMESPACE, "battery"), 1, (l, p) -> new BatteryModule(l), BatteryModule::new);
    public static final MeatgunModule.Type<HomingModule> HOMING_BRAIN = register(new Identifier(MeatWeapons.NAMESPACE, "homing_brain"), 4, (l, p) -> new HomingModule(l), HomingModule::new);

    public static final MeatgunModule.Type<HalberdModule> HALBERD = register(new Identifier(MeatWeapons.NAMESPACE, "halberd"), 6, (l, p) -> new HalberdModule(l), HalberdModule::new);
    public static final MeatgunModule.Type<ShockStaffModule> SHOCK_STAFF = register(new Identifier(MeatWeapons.NAMESPACE, "shock_staff"), 6, (l, p) -> new ShockStaffModule(l), ShockStaffModule::new);

    public static <T extends MeatgunModule> MeatgunModule.Type<T> register(Identifier id, int complexity, MeatgunModule.Factory<T> factory, MeatgunModule.NbtFactory<T> nbtFactory)
    {
        return Registry.register(REGISTRY, id, new MeatgunModule.Type<>(id, complexity, factory, nbtFactory));
    }

    static
    {
        Registry.register(REGISTRY, REGISTRY.getDefaultId(), MeatgunModule.DEFAULT_TYPE);
    }
}
