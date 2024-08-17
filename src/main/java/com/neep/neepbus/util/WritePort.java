package com.neep.neepbus.util;

public interface WritePort extends NeepBusPort
{
    void write(int data);

    /**
     * Anything that remembers a {@link WritePort} must register a listener here.
     * This allows ports to change and swap their addresses and notify caching senders without an expensive BFS.
     */
    @Deprecated
    default void addInvalidateListener(Runnable invalidate) {}
}
