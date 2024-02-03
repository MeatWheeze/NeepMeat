package com.neep.neepmeat.neepasm.compiler.variable;

public interface Variable<T>
{
    T value();

    Class<T> type();

    int compare(Variable<?> v2);

    default boolean notEmpty()
    {
        return !type().equals(Void.class);
    }

    Variable<Void> EMPTY = new Variable<>()
    {
        @Override
        public Void value()
        {
            return null;
        }

        @Override
        public Class<Void> type()
        {
            return Void.class;
        }

        @Override
        public int compare(Variable<?> v2)
        {
            return -2;
        }
    };
}
