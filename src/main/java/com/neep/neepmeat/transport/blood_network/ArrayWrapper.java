package com.neep.neepmeat.transport.blood_network;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class ArrayWrapper<T> implements Collection<T>
{
    private final T[] array;

    public ArrayWrapper(T[] array)
    {
        this.array = array;
    }

    @Override
    public int size()
    {
        return array.length;
    }

    @Override
    public boolean isEmpty()
    {
        return array.length == 0;
    }

    @Override
    public boolean contains(Object o)
    {
        return Arrays.binarySearch(array, o) != -1;
    }

    @NotNull
    @Override
    public Iterator<T> iterator()
    {
        return Arrays.stream(array).iterator();
    }

    @NotNull
    @Override
    public T[] toArray()
    {
        return array;
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean add(T t)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }
}
