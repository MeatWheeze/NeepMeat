package com.neep.meatlib.mixin;

import com.neep.meatlib.recipe.MeatRecipeManager;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.function.LootFunctionManager;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.TagManagerLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin
{
    @Final @Shadow private TagManagerLoader registryTagManager;
    @Final @Shadow private LootConditionManager lootConditionManager;
    @Final @Shadow private LootManager lootManager;
    @Final @Shadow private LootFunctionManager lootFunctionManager;
    @Final @Shadow private ServerAdvancementLoader serverAdvancementLoader;
    @Final @Shadow private FunctionLoader functionLoader;
    @Final @Shadow private RecipeManager recipeManager;

    @Inject(method = "getContents", at = @At(value = "TAIL"))
    public void getContents(CallbackInfoReturnable<List<ResourceReloader>> cir)
    {
//        List<ResourceReloader> old = List.of(this.meatRecipeManager, this.registryTagManager, this.lootConditionManager, this.recipeManager, this.lootManager, this.lootFunctionManager, this.functionLoader, this.serverAdvancementLoader);
//        cir.setReturnValue(old);
    }
}
