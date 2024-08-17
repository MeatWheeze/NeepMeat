package com.neep.neepbus.util;

import java.util.Map;

public record PortMap(Map<String, WritePort> writePorts, Map<String, ReadPort> readPorts)
{
    public static final PortMap EMPTY = new PortMap(Map.of(), Map.of());

    //    public record Entry<T>(String address, T port) implements Pair<String, T>
//    {
//        @Override
//        public String left()
//        {
//            return address;
//        }
//
//        @Override
//        public T right()
//        {
//            return port;
//        }
//    }
}
