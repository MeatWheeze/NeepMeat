package com.neep.neepbus.component;

import com.neep.neepbus.client.item.NetworkingToolClient;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import net.minecraft.item.ItemStack;

public class NetworkingToolComponent extends ItemComponent
{
    public NetworkingToolComponent(ItemStack stack)
    {
        super(stack);
    }

    public NetworkingToolClient.Mode getMode()
    {
        return NetworkingToolClient.Mode.values()[getInt("mode")];
    }
}
