package com.neep.neepmeat.compat.emi;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.compat.emi.helper.LazyEmiRecipeCategory;
import com.neep.neepmeat.compat.emi.recipe.*;
import com.neep.neepmeat.datagen.tag.NMTags;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.init.NMItems;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.plc.PLCBlocks;
import com.neep.neepmeat.plc.recipe.PLCRecipes;
import com.neep.neepmeat.plc.recipe.TransformingToolRecipe;
import com.neep.neepmeat.transport.FluidTransport;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

import java.util.List;

public class NMEmiPlugin implements EmiPlugin {
    public static final EmiStack ALLOY_SMELTING_WORKSTATION = EmiStack.of(NMBlocks.ALLOY_KILN);
    public static final EmiStack COMPACTING_WORKSTATION = EmiStack.of(NMBlocks.CHARNEL_COMPACTOR);
    public static final EmiStack ENLIGHTENING_WORKSTATION = EmiStack.of(NMBlocks.PEDESTAL);
    public static final EmiStack GRINDING_WORKSTATION = EmiStack.of(NMBlocks.GRINDER);
    public static final EmiStack VIVISECTION_WORKSTATION = EmiStack.of(NMItems.SACRIFICIAL_SCALPEL);
    public static final EmiStack HEATING_WORKSTATION = EmiStack.of(FluidTransport.MULTI_TANK);
    public static final EmiStack MANUFACTURE_WORKSTATION = EmiStack.of(PLCBlocks.PLC);
    public static final EmiStack MIXING_WORKSTATION = EmiStack.of(NMBlocks.MIXER);
    public static final EmiStack PRESSING_WORKSTATION = EmiStack.of(NMBlocks.HYDRAULIC_PRESS);
    public static final EmiStack SURGERY_WORKSTATION = EmiStack.of(PLCBlocks.PLC);
    public static final EmiStack TRANSFORMING_TOOL_WORKSTATION = EmiStack.of(PLCBlocks.PLC);
    public static final EmiStack TROMMEL_WORKSTATION = EmiStack.of(NMBlocks.SMALL_TROMMEL);

    public static final EmiRecipeCategory ALLOY_SMELTING = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/alloy_smelting"), ALLOY_SMELTING_WORKSTATION);
    public static final EmiRecipeCategory COMPACTING = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/compacting"), COMPACTING_WORKSTATION);
    public static final EmiRecipeCategory ENLIGHTENING = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/enlightening"), ENLIGHTENING_WORKSTATION);
    public static final EmiRecipeCategory GRINDING = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/grinding"), GRINDING_WORKSTATION);
    public static final EmiRecipeCategory VIVISECTION = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/vivisection"), VIVISECTION_WORKSTATION);
    public static final EmiRecipeCategory HEATING = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/heating"), HEATING_WORKSTATION);
    public static final EmiRecipeCategory MANUFACTURE = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/manufacture"), MANUFACTURE_WORKSTATION);
    public static final EmiRecipeCategory MIXING = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/mixing"), MIXING_WORKSTATION);
    public static final EmiRecipeCategory PRESSING = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/pressing"), PRESSING_WORKSTATION);
    public static final EmiRecipeCategory SURGERY = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/surgery"), SURGERY_WORKSTATION);
    public static final EmiRecipeCategory TRANSFORMING_TOOL = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/transforming_tool"), TRANSFORMING_TOOL_WORKSTATION);
    public static final EmiRecipeCategory TROMMEL = new LazyEmiRecipeCategory(new Identifier(NeepMeat.NAMESPACE, "plugins/trommel"), TROMMEL_WORKSTATION);

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(ALLOY_SMELTING);
        registry.addCategory(COMPACTING);
        registry.addCategory(ENLIGHTENING);
        registry.addCategory(GRINDING);
        registry.addCategory(VIVISECTION);
        registry.addCategory(HEATING);
        registry.addCategory(MANUFACTURE);
        registry.addCategory(MIXING);
        registry.addCategory(PRESSING);
        registry.addCategory(SURGERY);
        registry.addCategory(TRANSFORMING_TOOL);
        registry.addCategory(TROMMEL);

        registry.addWorkstation(ALLOY_SMELTING, ALLOY_SMELTING_WORKSTATION);
        registry.addWorkstation(COMPACTING, COMPACTING_WORKSTATION);
        registry.addWorkstation(ENLIGHTENING, ENLIGHTENING_WORKSTATION);
        registry.addWorkstation(GRINDING, GRINDING_WORKSTATION);
        registry.addWorkstation(VIVISECTION, VIVISECTION_WORKSTATION);
        registry.addWorkstation(HEATING, HEATING_WORKSTATION);
        registry.addWorkstation(MANUFACTURE, MANUFACTURE_WORKSTATION);
        registry.addWorkstation(TRANSFORMING_TOOL, TRANSFORMING_TOOL_WORKSTATION);
        registry.addWorkstation(MIXING, MIXING_WORKSTATION);
        registry.addWorkstation(PRESSING, PRESSING_WORKSTATION);
        registry.addWorkstation(SURGERY, SURGERY_WORKSTATION);
        registry.addWorkstation(TROMMEL, TROMMEL_WORKSTATION);

        RecipeManager manager = registry.getRecipeManager();
        manager.listAllOfType(NMrecipeTypes.ENLIGHTENING)
                .stream()
                .map(EnlighteningEmiRecipe::new)
                .forEach(registry::addRecipe);
        manager.listAllOfType(NMrecipeTypes.MIXING)
                .stream()
                .map(MixingEmiRecipe::new)
                .forEach(registry::addRecipe);
        manager.listAllOfType(NMrecipeTypes.PRESSING)
                .stream()
                .map(PressingEmiRecipe::new)
                .forEach(registry::addRecipe);

        MeatlibRecipes.getInstance().getAllValuesOfType(NMrecipeTypes.ALLOY_SMELTING)
                .map(AlloySmeltingEmiRecipe::new)
                .forEach(registry::addRecipe);
        MeatlibRecipes.getInstance().getAllValuesOfType(NMrecipeTypes.GRINDING)
                .map(GrindingEmiRecipe::new)
                .forEach(registry::addRecipe);
        MeatlibRecipes.getInstance().getAllValuesOfType(NMrecipeTypes.HEATING)
                .map(HeatingEmiRecipe::new)
                .forEach(registry::addRecipe);
        MeatlibRecipes.getInstance().getAllValuesOfType(PLCRecipes.MANUFACTURE)
                .map(ManufactureEmiRecipe::new)
                .forEach(registry::addRecipe);
        MeatlibRecipes.getInstance().getAllValuesOfType(NMrecipeTypes.TROMMEL)
                .map(TrommelEmiRecipe::new)
                .forEach(registry::addRecipe);

        registry.addRecipe(new ToolManufactureEmiRecipe(TransformingToolRecipe.getInstance()));

        // Charnel Compactor recipes
        int page = 0;
        UnmodifiableIterator<List<Item>> iterator = Iterators.partition(Registries.ITEM.getEntryList(NMTags.RAW_MEAT).orElseThrow().stream().map(RegistryEntry::value).iterator(), 35);
        while (iterator.hasNext()) {
            List<Item> entries = iterator.next();
            registry.addRecipe(new CompactingEmiRecipe(entries, NMItems.CRUDE_INTEGRATION_CHARGE, page++));
        }

        // Heart extraction
        registry.addRecipe(new VivisectionEmiRecipe(List.of(EntityType.ZOMBIE), NMItems.ANIMAL_HEART));
        registry.addRecipe(new VivisectionEmiRecipe(NMBlocks.INTEGRATOR_EGG.asItem(), NMItems.CHRYSALIS));
    }
}
