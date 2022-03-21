package com.neep.neepmeat.data;

import com.mojang.datafixers.DataFixer;
import net.minecraft.world.PersistentStateManager;

import java.io.File;

public class FluidNetworkPersistentState extends PersistentStateManager
{
    public FluidNetworkPersistentState(File directory, DataFixer dataFixer)
    {
        super(directory, dataFixer);
    }
}
