package com.neep.neepmeat.implant.player;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.util.Identifier;

import java.util.Set;

public interface ImplantManager extends Component
{
    void installImplant(Identifier implantId);
    void removeImplant(Identifier id);

    Set<Identifier> getInstalled();
}
