//package com.neep.neepmeat.arrp;
//
//import com.neep.meatlib.api.event.DataPackPostProcess;
//import com.neep.neepmeat.NeepMeat;
//import com.neep.neepmeat.api.processing.BlockCrushingRegistry;
//import net.devtech.arrp.api.RRPCallback;
//import net.devtech.arrp.api.RuntimeResourcePack;
//import net.minecraft.resource.ResourcePack;
//import net.minecraft.util.Identifier;
//
//import java.util.List;
//
//public class NMARRPThings
//{
//    public static void init()
//    {
//
//        RRPCallback.AFTER_VANILLA.register(NMARRPThings::createPack);
//    }
//
//    private static void createPack(List<ResourcePack> packs)
//    {
//        RuntimeResourcePack pack = RuntimeResourcePack.create(new Identifier(NeepMeat.NAMESPACE, "rrp"));
//        String recipe = new StringBuilder(
//                """
//{
//    "type": "neepmeat:advanced_crushing",
//  "input": {
//    "resource": "minecraft:stone",
//    "amount": 1
//  },
//  "output": {
//    "resource": "minecraft:netherite_scrap",
//    "amount": 1
//  },
//  "extra": {
//    "resource": "minecraft:netherite_scrap",
//    "amount": 1
//  },
//  "experience": 5,
//    "processtime": 40
//}
//            """).toString();
//        pack.addResource(ResourceType.SERVER_DATA, new Identifier(NeepMeat.NAMESPACE, "recipes/advanced_crushing_gen/test.json"), recipe.getBytes());
//        pack.addRecipe(new Identifier(NeepMeat.NAMESPACE, "glophis"), JRecipe.smelting(JIngredient.ingredient().item(NMItems.FARMING_SCUTTER), JResult.item(Items.SAND)).cookingTime(10));
//        NeepMeat.LOGGER.info("ADDING RECIPES");
//        packs.add(pack);
//    }
//}
