package com.neep.neepmeat.block.vat;

import com.neep.neepmeat.blockentity.machine.VatControllerBlockEntity;

public interface IVatStructure
{
    interface Entity
    {
        VatControllerBlockEntity getController();
        void setController(VatControllerBlockEntity controller);

        default boolean hasController()
        {
            return getController() != null;
        }
    }
}
