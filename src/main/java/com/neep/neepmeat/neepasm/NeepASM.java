package com.neep.neepmeat.neepasm;

public class NeepASM
{
    private static final String SOURCE = "main:\n MOVE @(1 2 3 N) @(4 5 6) oggins=ooe # Cromment";

    public static void main(String[] args)
    {
//        Compiler compiler = new Compiler();
//        compiler.compile(SOURCE);
    }

    public static class ParseException extends Exception
    {
        public ParseException(String message, String token)
        {
            super(message + ": " + token);
        }

        public ParseException(String message)
        {
            super(message);
        }

        public ParseException(String message, char token)
        {
            super(message + ": " + token);
        }
    }

    public static class CompilationException extends Exception
    {
        public CompilationException(String message)
        {
            super(message);
        }
    }
}
