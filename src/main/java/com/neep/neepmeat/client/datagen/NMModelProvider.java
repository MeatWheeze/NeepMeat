package com.neep.neepmeat.client.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.neep.meatlib.datagen.MeatLibDataGen;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.live_machine.LivingMachineBlock;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

public class NMModelProvider extends FabricModelProvider
{
    public NMModelProvider(FabricDataOutput output)
    {
        super(output);
    }

    public static void init()
    {
        MeatLibDataGen.register(NMModelProvider::new);
    }

    @Override
    public String getName()
    {
        return super.getName() + "(neepmeat)";
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator)
    {
//        Identifier modelId = new Identifier(NeepMeat.NAMESPACE, "block/painted_corrugated_asbestos");
        for (var block : NMBlocks.PAINTED_CORRUGATED_ASBESTOS.entries)
        {
            blockStateModelGenerator.registerSimpleCubeAll(block);
//            blockStateModelGenerator.blockStateCollector.accept(new SimpleBlockStateSupplier(block, modelId));
//            Identifier modelId = blockStateModelGenerator.
//            blockStateModelGenerator.blockStateCollector.accept(new SimpleBlockStateSupplier(block, ));
//            blockStateModelGenerator.item(block);
        }

        blockStateModelGenerator.registerSingleton(LivingMachines.SKIN_MACHINE_BLOCK,
                TextureMap.all(new Identifier(NeepMeat.NAMESPACE, "block/living_machine/skin_machine_block")),
                TexturedModel.CUBE_ALL.get(LivingMachines.SKIN_MACHINE_BLOCK).getModel());

//        blockStateModelGenerator.registerSingleton(NMBlocks.ACTIVE_WASTE,
//                TextureMap.all(new Identifier(NeepMeat.NAMESPACE, "block/reactor/active_waste_1")),
//                TexturedModel.CUBE_ALL.get(NMBlocks.ACTIVE_WASTE).getModel());
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator)
    {
    }

    public static class SimpleBlockStateSupplier implements BlockStateSupplier
    {
        private final Block block;
        private final Identifier model;

        public SimpleBlockStateSupplier(Block block, Identifier model)
        {
            this.block = block;
            this.model = model;
        }

        @Override
        public Block getBlock()
        {
            return block;
        }

        @Override
        public JsonElement get()
        {
            JsonObject root = new JsonObject();
            JsonObject variant = new JsonObject();
            JsonObject model = new JsonObject();

            model.addProperty("model", this.model.toString());
            variant.add("", model);
            root.add("variants", variant);
            return root;
        }

    }
}
