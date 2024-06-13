package com.neep.meatlib.util;

/**
 * An object containing logic and fields that can only exist on the client that can be referenced in an
 * Entity or BlockEntity via an opaque reference.
 */
public interface ClientComponent
{
    void clientTick();
}
