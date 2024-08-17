package com.neep.neepbus.util;

public interface ReadPort extends NeepBusPort
{
    int read();

    /**
     * Anything that remembers a {@link ReadPort} must register a listener here.
     * This allows ports to change and swap their addresses and notify caching senders without an expensive BFS.
     */
//    @Deprecated
//    void addInvalidateListener(Runnable invalidate);
}
