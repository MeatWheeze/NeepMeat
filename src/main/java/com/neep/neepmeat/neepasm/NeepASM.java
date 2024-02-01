package com.neep.neepmeat.neepasm;

import com.neep.neepmeat.neepasm.compiler.Compiler;

public class NeepASM
{
    private static final String SOURCE = "main:\n MOVE @(1 2 3 N) @(4 5 6) oggins=ooe # Cromment";

    public static void main(String[] args)
    {
        Compiler compiler = new Compiler();
        compiler.parse(SOURCE);
    }

    public static class NeepASMParseException extends Exception
    {
        public NeepASMParseException(String message, String token)
        {
            super(message + ": " + token);
        }

        public NeepASMParseException(String message)
        {
            super(message);
        }

        public NeepASMParseException(String message, char token)
        {
            super(message + ": " + token);
        }
    }
}
