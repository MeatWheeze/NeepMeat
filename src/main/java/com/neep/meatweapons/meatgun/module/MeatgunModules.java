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

    public static final MeatgunModule.Type<BasePistolModule> BASE_PISTOL = register(new Identifier(MeatWeapons.NAMESPACE, "base_pistol"), (l, p) -> new BasePistolModule(l), BasePistolModule::fromNbt);
    public static final MeatgunModule.Type<BaseStaffModule> BASE_STAFF = register(new Identifier(MeatWeapons.NAMESPACE, "base_staff"), (l, p) -> new BaseStaffModule(l), BaseStaffModule::fromNbt);
    public static final MeatgunModule.Type<PistolModule> PISTOL = register(new Identifier(MeatWeapons.NAMESPACE, "pistol"), (l, p) -> new PistolModule(l), PistolModule::new);
    public static final MeatgunModule.Type<ChuggerModule> CHUGGER = register(new Identifier(MeatWeapons.NAMESPACE, "chugger"), (l, p) -> new ChuggerModule(l), ChuggerModule::new);
    public static final MeatgunModule.Type<BosherModule> BOSHER = register(new Identifier(MeatWeapons.NAMESPACE, "bosher"), (l, p) -> new BosherModule(l), BosherModule::new);
    public static final MeatgunModule.Type<LongBoiModule> LONG_BOI = register(new Identifier(MeatWeapons.NAMESPACE, "long_boi"), (l, p) -> new LongBoiModule(l), LongBoiModule::new);
    public static final MeatgunModule.Type<GrenadeLauncherModule> GRENADE_LAUNCHER = register(new Identifier(MeatWeapons.NAMESPACE, "grenade_launcher"), (l, p) -> new GrenadeLauncherModule(l), GrenadeLauncherModule::new);
    public static final MeatgunModule.Type<BloodthrowerModule> BLOODTHROWER = register(new Identifier(MeatWeapons.NAMESPACE, "bloodthrower"), (l, p) -> new BloodthrowerModule(l), BloodthrowerModule::new);
    public static final MeatgunModule.Type<TripleCarouselModule> TRIPLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "triple_carousel"), (l, p) -> new TripleCarouselModule(l), TripleCarouselModule::new);
    public static final MeatgunModule.Type<DoubleCarouselModule> DOUBLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "double_carousel"), (l, p) -> new DoubleCarouselModule(l), DoubleCarouselModule::new);
    public static final MeatgunModule.Type<UnderbarrelModule> UNDERBARREL = register(new Identifier(MeatWeapons.NAMESPACE, "underbarrel"), (l, p) -> new UnderbarrelModule(l), UnderbarrelModule::new);

    public static final MeatgunModule.Type<BatteryModule> BATTERY = register(new Identifier(MeatWeapons.NAMESPACE, "battery"), (l, p) -> new BatteryModule(l), BatteryModule::new);

    public static final MeatgunModule.Type<HalberdModule> HALBERD = register(new Identifier(MeatWeapons.NAMESPACE, "halberd"), (l, p) -> new HalberdModule(l), HalberdModule::new);
    public static final MeatgunModule.Type<ShockStaffModule> SHOCK_STAFF = register(new Identifier(MeatWeapons.NAMESPACE, "shock_staff"), (l, p) -> new ShockStaffModule(l), ShockStaffModule::new);

    public static <T extends MeatgunModule> MeatgunModule.Type<T> register(Identifier id, MeatgunModule.Factory<T> factory, MeatgunModule.NbtFactory<T> nbtFactory)
    {
        return Registry.register(REGISTRY, id, new MeatgunModule.Type<>(id, factory, nbtFactory));
    }

    static
    {
        Registry.register(REGISTRY, REGISTRY.getDefaultId(), MeatgunModule.DEFAULT_TYPE);
    }
}
