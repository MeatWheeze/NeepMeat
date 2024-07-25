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
    public static final MeatgunModule.Type<PistolModule> PISTOL = register(new Identifier(MeatWeapons.NAMESPACE, "pistol"),
            new AttributeContainer().complexity(4).cooldown(10).attackDamage(4).consume(1), (l, p) -> new PistolModule(l), PistolModule::new);
    public static final MeatgunModule.Type<ChuggerModule> CHUGGER = register(new Identifier(MeatWeapons.NAMESPACE, "chugger"),
            new AttributeContainer().complexity(6).cooldown(15).consume(4).attackDamage(7), (l, p) -> new ChuggerModule(l), ChuggerModule::new);
    public static final MeatgunModule.Type<BosherModule> BOSHER = register(new Identifier(MeatWeapons.NAMESPACE, "bosher"),
            new AttributeContainer().complexity(8).cooldown(20).consume(8).attackDamage(4).consume(8), (l, p) -> new BosherModule(l), BosherModule::new);
    public static final MeatgunModule.Type<LongBoiModule> LONG_BOI = register(new Identifier(MeatWeapons.NAMESPACE, "long_boi"),
            new AttributeContainer().complexity(10).cooldown(30).consume(16).attackDamage(25), (l, p) -> new LongBoiModule(l), LongBoiModule::new);
    public static final MeatgunModule.Type<GrenadeLauncherModule> GRENADE_LAUNCHER = register(new Identifier(MeatWeapons.NAMESPACE, "grenade_launcher"),
            new AttributeContainer().complexity(6).cooldown(15).consume(4), (l, p) -> new GrenadeLauncherModule(l), GrenadeLauncherModule::new);
    public static final MeatgunModule.Type<BloodthrowerModule> BLOODTHROWER = register(new Identifier(MeatWeapons.NAMESPACE, "bloodthrower"),
            new AttributeContainer().complexity(8).cooldown(2).consume(1).attackDamage(2), (l, p) -> new BloodthrowerModule(l), BloodthrowerModule::new);
    public static final MeatgunModule.Type<TripleCarouselModule> TRIPLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "triple_carousel"),
            new AttributeContainer().complexity(2), (l, p) -> new TripleCarouselModule(l), TripleCarouselModule::new);
    public static final MeatgunModule.Type<DoubleCarouselModule> DOUBLE_CAROUSEL = register(new Identifier(MeatWeapons.NAMESPACE, "double_carousel"),
            new AttributeContainer().complexity(2), (l, p) -> new DoubleCarouselModule(l), DoubleCarouselModule::new);
    public static final MeatgunModule.Type<UnderbarrelModule> UNDERBARREL = register(new Identifier(MeatWeapons.NAMESPACE, "underbarrel"),
            new AttributeContainer().complexity(2), (l, p) -> new UnderbarrelModule(l), UnderbarrelModule::new);

    public static final MeatgunModule.Type<BatteryModule> BATTERY = register(new Identifier(MeatWeapons.NAMESPACE, "battery"),
            new AttributeContainer().complexity(1), (l, p) -> new BatteryModule(l), BatteryModule::new);
    public static final MeatgunModule.Type<HomingModule> HOMING_BRAIN = register(new Identifier(MeatWeapons.NAMESPACE, "homing_brain"),
            new AttributeContainer().complexity(4), (l, p) -> new HomingModule(l), HomingModule::new);

    public static final MeatgunModule.Type<HalberdModule> HALBERD = register(new Identifier(MeatWeapons.NAMESPACE, "halberd"),
            new AttributeContainer().complexity(6).attackDamage(7), (l, p) -> new HalberdModule(l), HalberdModule::new);
    public static final MeatgunModule.Type<ShockStaffModule> SHOCK_STAFF = register(new Identifier(MeatWeapons.NAMESPACE, "shock_staff"),
            new AttributeContainer().complexity(6).attackDamage(7), (l, p) -> new ShockStaffModule(l), ShockStaffModule::new);

    public static <T extends MeatgunModule> MeatgunModule.Type<T> register(Identifier id, AttributeContainer attributes, MeatgunModule.Factory<T> factory, MeatgunModule.NbtFactory<T> nbtFactory)
    {
        return Registry.register(REGISTRY, id, new MeatgunModule.Type<>(id, attributes, factory, nbtFactory));
    }

    public static <T extends MeatgunModule> MeatgunModule.Type<T> register(Identifier id, MeatgunModule.Factory<T> factory, MeatgunModule.NbtFactory<T> nbtFactory)
    {
        return Registry.register(REGISTRY, id, new MeatgunModule.Type<>(id, factory, nbtFactory));
    }

    static
    {
        Registry.register(REGISTRY, REGISTRY.getDefaultId(), MeatgunModule.DEFAULT_TYPE);
    }

}
