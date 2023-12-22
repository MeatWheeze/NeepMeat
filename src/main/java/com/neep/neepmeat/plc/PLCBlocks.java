package com.neep.neepmeat.plc;

import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.surgical_controller.TableControllerBlock;
import com.neep.neepmeat.plc.arm.RoboticArmBlock;
import com.neep.neepmeat.plc.arm.RoboticArmBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;

public class PLCBlocks
{
    public static BlockEntityType<RoboticArmBlockEntity> ROBOTIC_ARM_ENTITY;

    public static final Block ROBOTIC_ARM = BlockRegistry.queue(new RoboticArmBlock("robotic_arm", FabricBlockSettings.of(Material.METAL)));
    public static Block SURGERY_CONTROLLER = BlockRegistry.queue(new TableControllerBlock("surgery_controller", NMBlocks.block().requiresVascular(), FabricBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS)));

    public static void init()
    {
        ROBOTIC_ARM_ENTITY = NMBlockEntities.register("robotic_arm", (pos, state) -> new RoboticArmBlockEntity(ROBOTIC_ARM_ENTITY, pos, state), ROBOTIC_ARM);
    }
}
