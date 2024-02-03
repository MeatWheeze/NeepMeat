package com.neep.neepmeat.neepasm.compiler;

public class TokenView
{
    private final String line;
    private int offset = 0;

    public TokenView(String line)
    {
        this.line = line;
    }

    public int pos()
    {
        return offset;
    }

    public char next()
    {
        if (offset >= line.length())
            return 0;

        return line.charAt(offset++);
    }

    public void nextLine()
    {
        while (peek() != '\n' && offset < line.length())
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
        if (offset >= line.length())
            return 0;

        return line.charAt(offset);
    }

    public char peek(int n)
    {
        if (offset + n >= line.length())
            return 0;

        return line.charAt(offset + n);
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

    public Integer nextInteger()
    {
        fastForward();
        StringBuilder builder = new StringBuilder();
        while (peek() == '-' || Character.isDigit(peek()))
        {
            builder.append(next());
        }

        return Integer.parseInt(builder.toString());
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

    public boolean lineEnded()
    {
        return peek() == '\n' || peek() == 0;
    }

    private boolean isNotLineEnd(char c)
    {
        return c != '\n' && Character.isWhitespace(c) || c == 0;
    }

    public boolean eof()
    {
        return offset >= line.length();
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
