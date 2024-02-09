package com.neep.neepmeat.neepasm.compiler;

import com.neep.neepmeat.neepasm.NeepASM;

public class TokenView
{
    private final String string;
    private int offset = 0;

    public TokenView(String string)
    {
        this.string = string;
    }

    public int pos()
    {
        return offset;
    }

    public int line()
    {
        return (int) string.substring(0, offset).lines().count() - 1;
    }

    public int linePos()
    {
        int lastLine = string.substring(0, offset).lastIndexOf('\n');
        return offset - lastLine - 1;
    }

    public char next()
    {
        if (offset >= string.length())
            return 0;

        char c = string.charAt(offset++);

//        if (c == '\n')
//            lines++;

        return c;
    }

    public void nextLine()
    {
        while (peek() != '\n' && peek() != ';' && offset < string.length())
            next();

        next();
    }

    public char nextThing()
    {
        fastForward();
        return next();
    }

    public char peekThing()
    {
        fastForward();
        return peek();
    }

    public char peek()
    {
        if (offset >= string.length())
            return 0;

        return string.charAt(offset);
    }

    public char peek(int n)
    {
        if (offset + n >= string.length())
            return 0;

        return string.charAt(offset + n);
    }

    public void fastForward()
    {
        while (peek() != 0 && isNotLineEnd(peek()))
            next();
    }

    public String nextBlob()
    {
        fastForward();
        int n = 0;
        StringBuilder builder = new StringBuilder();
        while (peek(n) != 0 && !isNotLineEnd(peek(n)))
        {
            builder.append(peek(n++));
        }
        return builder.toString();
    }

    public String nextIdentifier()
    {
        fastForward();
        int index = 0;
        StringBuilder builder = new StringBuilder();
        while (isIdentifier(index, peek()))
        {
            builder.append(next());
            index++;
        }
        return builder.toString();
    }

    public Integer nextInteger() throws NeepASM.ParseException
    {
        fastForward();
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (isDigit(index, peek()))
        {
            builder.append(next());
            index++;
        }

        String s = builder.toString();

        if (s.equals("-"))
            return 0;

        int radix = 10;
        if (s.startsWith("0x"))
            radix = 16;
        else if (s.startsWith("0b"))
            radix = 2;

        // Not sure if this try-catch should be here or in the parser
        try
        {
            return Integer.parseInt(radix == 10 ? builder.toString() : builder.substring(2), radix);
        }
        catch (NumberFormatException e)
        {
            throw new NeepASM.ParseException(e.getMessage());
        }
    }

    public String nextString()
    {
        while (peek() != '"')
        {
            if (lineEnded())
                return "";
            next();
        }
        next();

        StringBuilder builder = new StringBuilder();
        while (peek() != '"')
        {
            if (lineEnded())
                return "";

            builder.append(next());
        }
        return builder.toString();
    }

    public Entry save()
    {
        return new Entry(offset);
    }

    public static boolean isIdentifier(int index, char c)
    {
        return c == '_' || Character.isAlphabetic(c) || (index != 0 && (Character.isDigit(c)));
    }

    public static boolean isDigit(char c)
    {
        return  c == '-' || Character.isDigit(c);
    }

    private static boolean isDigit(int index, char c)
    {
        // Handle hex or bin prefixes
        return (index == 1 && (c == 'x' || c == 'b')) || isDigit(c);
    }

    public boolean lineEnded()
    {
        return peek() == '\n' || peek() == ';' || peek() == 0;
    }

    private boolean isNotLineEnd(char c)
    {
        return (c != '\n' && c != ';') && Character.isWhitespace(c) || c == 0;
    }

    public boolean eof()
    {
        return offset >= string.length();
    }

    public class Entry implements AutoCloseable
    {
        int saved;
        boolean commit = false;

        public Entry(int saved)
        {
            this.saved = saved;
        }

        public void commit()
        {
            commit = true;
        }

        @Override
        public void close()
        {
            if (!commit)
                offset = saved;
        }
    }
}
