package com.neep.neepmeat.transport.blood_network;

import com.neep.neepmeat.transport.api.pipe.BloodAcceptor;
import org.jetbrains.annotations.Nullable;

public interface BloodTransferChangeListener
{
    void updateTransfer(@Nullable BloodAcceptor changed);
}
