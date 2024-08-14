package com.neep.neepbus.util;

public interface NeepBusPort
{
    void receive(int data);

    /**
     * Anything that remembers a {@link NeepBusPort} must register a listener here.
     * This allows ports to change and swap their addresses and notify caching senders without an expensive BFS.
     */
    void addInvalidateListener(Runnable invalidate);
}
