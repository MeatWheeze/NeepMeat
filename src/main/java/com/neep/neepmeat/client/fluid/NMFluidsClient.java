package com.neep.neepmeat.client.fluid;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.model.GenericModel;
import com.neep.neepmeat.client.renderer.MeatSteelArmourRenderer;
import com.neep.neepmeat.client.renderer.SwordRenderer;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

@Environment(value= EnvType.CLIENT)
public class NMFluidsClient
{
    public static final Identifier BLOOD_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/blood_flowing");
    public static final Identifier BLOOD = new Identifier(NeepMeat.NAMESPACE, "block/blood_still");
    public static final Identifier CHARGED_WORK_FLUID_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/charged_work_fluid_flowing");
    public static final Identifier CHARGED_WORK_FLUID= new Identifier(NeepMeat.NAMESPACE, "block/charged_work_fluid_still");
    public static final Identifier WORK_FLUID_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/work_fluid_flowing");
    public static final Identifier WORK_FLUID = new Identifier(NeepMeat.NAMESPACE, "block/work_fluid_still");
    public static final Identifier ETHEREAL_FUEL_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/ethereal_fuel_flowing");
    public static final Identifier ETHEREAL_FUEL = new Identifier(NeepMeat.NAMESPACE, "block/ethereal_fuel_still");
    public static final Identifier ELDRITCH_ENZYMES_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/eldritch_enzymes_flowing");
    public static final Identifier ELDRITCH_ENZYMES = new Identifier(NeepMeat.NAMESPACE, "block/eldritch_enzymes_still");
    public static final Identifier DIRTY_ORE_FAT_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/dirty_ore_fat_flowing");
    public static final Identifier DIRTY_ORE_FAT= new Identifier(NeepMeat.NAMESPACE, "block/dirty_ore_fat_still");
    public static final Identifier CLEAN_ORE_FAT_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/clean_ore_fat_flowing");
    public static final Identifier CLEAN_ORE_FAT= new Identifier(NeepMeat.NAMESPACE, "block/clean_ore_fat_still");
    public static final Identifier MEAT = new Identifier(NeepMeat.NAMESPACE, "block/meat_still");
    public static final Identifier MEAT_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/meat_flowing");

    public static final Identifier TISSUE_SLURRY_STILL = new Identifier(NeepMeat.NAMESPACE, "block/tissue_slurry_still");
    public static final Identifier TISSUE_SLURRY_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/tissue_slurry_flowing");
    public static final Identifier MILK = new Identifier(NeepMeat.NAMESPACE, "block/milk_still");
    public static final Identifier MILK_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/milk_flowing");
    public static final Identifier FEED = new Identifier(NeepMeat.NAMESPACE, "block/animal_feed_still");
    public static final Identifier FEED_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/animal_feed_flowing");
    public static final Identifier PINKDRINK = new Identifier(NeepMeat.NAMESPACE, "block/pinkdrink_still");
    public static final Identifier PINKDRINK_FLOWING = new Identifier(NeepMeat.NAMESPACE, "block/pinkdrink_flowing");

    public static void registerFluidRenderers()
    {
        GeoItemRenderer.registerItemRenderer(NMItems.SLASHER, new SwordRenderer<>(new GenericModel<>(
                NeepMeat.NAMESPACE,
                "geo/slasher.geo.json",
                "textures/item/slasher.png",
                "animations/slasher.animation.json"
        )));

        GeoItemRenderer.registerItemRenderer(NMItems.CHEESE_CLEAVER, new SwordRenderer<>(new GenericModel<>(
                NeepMeat.NAMESPACE,
                "geo/cheese_cleaver.geo.json",
                "textures/item/cheese_cleaver.png",
                "animations/cheese_cleaver.animation.json"
        )));

        GeoArmorRenderer.registerArmorRenderer(new MeatSteelArmourRenderer(new GenericModel<>(
                        NeepMeat.NAMESPACE,
                        "geo/meat_steel_armour.geo.json",
                        "textures/entity/armour/meat_steel_armour.png",
                        "animations/meat_steel_armour.animation.json"
                )),
                NMItems.MEAT_STEEL_BOOTS, NMItems.MEAT_STEEL_LEGS, NMItems.MEAT_STEEL_CHESTPLATE);

        // Fluid textures
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_BLOOD, NMFluids.FLOWING_BLOOD, new SimpleFluidRenderHandler(
                BLOOD,
                BLOOD_FLOWING
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_PATINA_TREATMENT, NMFluids.FLOWING_PATINA_TREATMENT, new SimpleFluidRenderHandler(
                new Identifier("minecraft:block/water_still"),
                new Identifier("minecraft:block/water_flow"),
                0x4db99a
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_WORK_FLUID, NMFluids.FLOWING_WORK_FLUID, new SimpleFluidRenderHandler(
                WORK_FLUID,
                WORK_FLUID_FLOWING,
                0x999999
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_CHARGED_WORK_FLUID, NMFluids.FLOWING_CHARGED_WORK_FLUID, new SimpleFluidRenderHandler(
                CHARGED_WORK_FLUID,
                CHARGED_WORK_FLUID_FLOWING,
                0xFFFFFF
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_ETHEREAL_FUEL, NMFluids.FLOWING_ETHEREAL_FUEL, new SimpleFluidRenderHandler(
                ETHEREAL_FUEL,
                ETHEREAL_FUEL_FLOWING,
                0xFFFFFF
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_ELDRITCH_ENZYMES, NMFluids.FLOWING_ELDRITCH_ENZYMES, new SimpleFluidRenderHandler(
                ELDRITCH_ENZYMES,
                ELDRITCH_ENZYMES_FLOWING,
                0x3657a2
        ));

        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_DIRTY_ORE_FAT, NMFluids.FLOWING_DIRTY_ORE_FAT, new SimpleFluidRenderHandler(DIRTY_ORE_FAT, DIRTY_ORE_FAT_FLOWING, 0x3657a2));
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_CLEAN_ORE_FAT, NMFluids.FLOWING_CLEAN_ORE_FAT, new SimpleFluidRenderHandler(CLEAN_ORE_FAT, CLEAN_ORE_FAT_FLOWING, 0x3657a2));
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_MEAT, NMFluids.FLOWING_MEAT, new SimpleFluidRenderHandler(MEAT, MEAT_FLOWING));
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_C_MEAT, NMFluids.FLOWING_C_MEAT, new SimpleFluidRenderHandler(MEAT, MEAT_FLOWING));
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_TISSUE_SLURRY, NMFluids.FLOWING_TISSUE_SLURRY, new SimpleFluidRenderHandler(TISSUE_SLURRY_STILL, TISSUE_SLURRY_FLOWING));
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_MILK, NMFluids.FLOWING_MILK, new SimpleFluidRenderHandler(MILK, MILK_FLOWING));
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_P_MILK, NMFluids.FLOWING_P_MILK, new SimpleFluidRenderHandler(MILK, MILK_FLOWING));
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_FEED, NMFluids.FLOWING_FEED, new SimpleFluidRenderHandler(FEED, FEED_FLOWING));
        FluidRenderHandlerRegistry.INSTANCE.register(NMFluids.STILL_PINKDRINK, NMFluids.FLOWING_PINKDRINK, new SimpleFluidRenderHandler(PINKDRINK, PINKDRINK_FLOWING, 0xd28dab));

        FluidVariantRendering.register(NMFluids.STILL_DIRTY_ORE_FAT, new OreFatFluidVariantRenderHandler());
        FluidVariantRendering.register(NMFluids.FLOWING_DIRTY_ORE_FAT, new OreFatFluidVariantRenderHandler());
        FluidVariantAttributes.register(NMFluids.STILL_DIRTY_ORE_FAT, new OreFatAttributeHandler());
        FluidVariantAttributes.register(NMFluids.FLOWING_DIRTY_ORE_FAT, new OreFatAttributeHandler());

        FluidVariantRendering.register(NMFluids.STILL_CLEAN_ORE_FAT, new OreFatFluidVariantRenderHandler());
        FluidVariantRendering.register(NMFluids.FLOWING_CLEAN_ORE_FAT, new OreFatFluidVariantRenderHandler());
        FluidVariantAttributes.register(NMFluids.STILL_CLEAN_ORE_FAT, new OreFatAttributeHandler());
        FluidVariantAttributes.register(NMFluids.FLOWING_CLEAN_ORE_FAT, new OreFatAttributeHandler());

        FluidVariantRendering.register(NMFluids.STILL_C_MEAT, new CoarseMeatVariantRenderHandler());
        FluidVariantRendering.register(NMFluids.FLOWING_C_MEAT, new CoarseMeatVariantRenderHandler());
        FluidVariantAttributes.register(NMFluids.STILL_C_MEAT, new MeatAttribtuteHandler());
        FluidVariantAttributes.register(NMFluids.FLOWING_C_MEAT, new MeatAttribtuteHandler());

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) ->
        {
            registry.register(WORK_FLUID);
            registry.register(WORK_FLUID_FLOWING);
            registry.register(CHARGED_WORK_FLUID);
            registry.register(CHARGED_WORK_FLUID_FLOWING);
            registry.register(ETHEREAL_FUEL);
            registry.register(ETHEREAL_FUEL_FLOWING);
            registry.register(ELDRITCH_ENZYMES);
            registry.register(ELDRITCH_ENZYMES_FLOWING);
            registry.register(DIRTY_ORE_FAT);
            registry.register(DIRTY_ORE_FAT_FLOWING);
            registry.register(CLEAN_ORE_FAT);
            registry.register(CLEAN_ORE_FAT_FLOWING);
            registry.register(BLOOD);
            registry.register(BLOOD_FLOWING);
            registry.register(MEAT);
            registry.register(MEAT_FLOWING);
            registry.register(TISSUE_SLURRY_STILL);
            registry.register(TISSUE_SLURRY_FLOWING);
            registry.register(MILK);
            registry.register(MILK_FLOWING);
            registry.register(FEED);
            registry.register(FEED_FLOWING);
            registry.register(PINKDRINK);
            registry.register(PINKDRINK_FLOWING);
        });

    }
}
