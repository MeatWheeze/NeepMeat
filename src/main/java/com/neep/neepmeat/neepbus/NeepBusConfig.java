package com.neep.neepmeat.neepbus;

import java.util.Map;

public interface NeepBusConfig
{
    Map<String, NeepBusPort> getInputPorts();

    interface Entry
    {
        String getName();
        String getAddress();
        void setAddress(String address);
    }

    class SimpleEntry implements Entry
    {
        private final String name;
        private String address;

        public SimpleEntry(String name)
        {
            this.name = name;
            this.address = name.toLowerCase();
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public String getAddress()
        {
            return address;
        }

        @Override
        public void setAddress(String address)
        {
            this.address = address;
        }
    }
}
