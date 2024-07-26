package com.neep.neepmeat.api.live_machine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@FunctionalInterface
public interface StructurePropertyFormatter
{
    DecimalFormat DEFAULT_FLOAT_FORMAT = new DecimalFormat("####.###");
    DecimalFormat E_FLOAT_FORMAT = new DecimalFormat("#.###E0");

    String format(float value);

    static String formatRepair(float repair)
    {
        float percentRepairPerTick = repair * 20 * 100;
        if (!Float.isFinite(percentRepairPerTick))
            return "?";

        BigDecimal bigDecimal = new BigDecimal(percentRepairPerTick).setScale(3, RoundingMode.HALF_UP);
        return bigDecimal.toEngineeringString() + "%/s";
    }
}
