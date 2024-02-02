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
        while (peek() != 0 && Character.isWhitespace(peek()))
            next();
    }

    public String nextBlob()
    {
        fastForward();
        int n = 0;
        StringBuilder builder = new StringBuilder();
        while (peek(n) != 0 && !Character.isWhitespace(peek(n)))
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
        while (Character.isAlphabetic(peek()) || (index != 0 && (Character.isDigit(peek()))))
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

    public Entry save()
    {
        return new Entry(offset);
    }

    public boolean lineEnded()
    {
        return peek() == 0;
    }

    public class Entry implements AutoCloseable
    {
        int saved;

        public Entry(int saved)
        {
            this.saved = saved;
        }

        public void commit()
        {
            saved = offset;
        }

        @Override
        public void close()
        {
            offset = saved;
        }
    }
}
