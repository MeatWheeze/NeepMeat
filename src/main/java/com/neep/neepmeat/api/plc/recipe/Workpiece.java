package com.neep.neepmeat.api.plc.recipe;

import dev.onyxstudios.cca.api.v3.component.Component;

import java.util.List;

public interface Workpiece extends Component
{
    void addStep(ManufactureStep<?> step);
    List<ManufactureStep<?>> getSteps();

    void clearSteps();

    void removeStep(int i);
}
