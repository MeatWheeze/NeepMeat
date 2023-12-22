package com.neep.neepmeat.plc;

import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMBlocks;
import com.neep.neepmeat.machine.surgical_controller.PLCBlock;
import com.neep.neepmeat.plc.arm.RoboticArmBlock;
import com.neep.neepmeat.plc.arm.RoboticArmBlockEntity;
import com.neep.neepmeat.plc.block.PLCRedstoneInterface;
import com.neep.neepmeat.plc.block.PLCRedstoneInterfaceBlockEntity;
import com.neep.neepmeat.plc.block.RedstoneInterface;
import com.neep.neepmeat.plc.instruction.gui.InstructionAttributes;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;

public class PLCBlocks
{
    public static BlockEntityType<PLCRedstoneInterfaceBlockEntity> REDSTONE_INTERFACE_ENTITY;
    public static BlockEntityType<RoboticArmBlockEntity> ROBOTIC_ARM_ENTITY;

    public static final Block ROBOTIC_ARM = BlockRegistry.queue(new RoboticArmBlock("robotic_arm", FabricBlockSettings.of(Material.METAL)));
    public static final Block REDSTONE_INTERFACE = BlockRegistry.queue(new PLCRedstoneInterface("redstone_interface", FabricBlockSettings.of(Material.METAL)));
    public static final Block PLC = BlockRegistry.queue(new PLCBlock("plc", NMBlocks.block(), FabricBlockSettings.copyOf(NMBlocks.MACHINE_SETTINGS)));
    public static BlockEntityType<PLCBlockEntity> PLC_ENTITY;

    public static void init()
    {
        ROBOTIC_ARM_ENTITY = NMBlockEntities.register("robotic_arm", (pos, state) -> new RoboticArmBlockEntity(ROBOTIC_ARM_ENTITY, pos, state), ROBOTIC_ARM);
        REDSTONE_INTERFACE_ENTITY = NMBlockEntities.register("redstone_interface", (pos, state) -> new PLCRedstoneInterfaceBlockEntity(REDSTONE_INTERFACE_ENTITY, pos, state), REDSTONE_INTERFACE);
        RedstoneInterface.LOOKUP.registerSelf(REDSTONE_INTERFACE_ENTITY);
        PLCBlocks.PLC_ENTITY = NMBlockEntities.register("plc", (pos, state) -> new PLCBlockEntity(PLCBlocks.PLC_ENTITY, pos, state), PLCBlocks.PLC);

        InstructionAttributes.init();
    }
}
