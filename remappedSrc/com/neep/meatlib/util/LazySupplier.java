package com.neep.meatlib.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * This is not indended for optimisation, but rather for situations where lazy initialisation is the only option.
 * An example could be a field in a BlockEntity that requires a World instance to be initialised.
 */
public interface LazySupplier<T> extends Supplier<T>
{
    @Override
    @NotNull
    T get();

    boolean isInitialised();

    void invalidate();

    static <T> LazySupplier<T> of(Supplier<T> supplier)
    {
        return new LazySupplierImpl<>(supplier);
    }

    class LazySupplierImpl<T> implements LazySupplier<T>
    {
        protected final Supplier<T> supplier;
        protected T object;
        public LazySupplierImpl(Supplier<T> supplier)
        {
            this.supplier = supplier;
        }

        @Override
        public T get()
        {
            if (object == null)
            {
                object = supplier.get();
                if (object == null) throw new IllegalStateException("Null references are not allowed.");
            }

            return object;
        }

        @Override
        public boolean isInitialised()
        {
            return object != null;
        }

        @Override
        public void invalidate()
        {
            object = null;
        }
    }
}
