package com.neep.neepmeat.api.plc.instruction;

public class InstructionException extends Exception
{
    public InstructionException(String thing)
    {
        super("Invalid argument: " + thing);
    }
}
