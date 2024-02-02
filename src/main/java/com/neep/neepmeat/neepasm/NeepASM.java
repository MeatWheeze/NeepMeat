package com.neep.neepmeat.neepasm;

import com.neep.neepmeat.neepasm.compiler.TokenView;

public class NeepASM
{
    private static final String SOURCE = "main:\n MOVE @(1 2 3 N) @(4 5 6) oggins=ooe # Cromment";

    public static void main(String[] args)
    {
//        Compiler compiler = new Compiler();
//        compiler.compile(SOURCE);
    }

    public static class ProgramException extends Exception
    {
        public final int line;
        public final int pos;

        public ProgramException(int line, int pos, String message)
        {
            super(message);
            this.line = line;
            this.pos = pos;
        }

        @Override
        public String getMessage()
        {
            return "At line " + line + ", char " + pos + ": " + super.getMessage();
        }
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
