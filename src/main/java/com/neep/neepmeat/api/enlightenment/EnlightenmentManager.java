package com.neep.neepmeat.api.enlightenment;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface EnlightenmentManager extends Component
{
    int getTotal();

    void exposeDose(float base, double sqDistance, double split);

    void addChronic(float amount);
}
