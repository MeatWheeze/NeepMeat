package com.neep.neepmeat.api.live_machine;

import java.util.Collection;
import java.util.Optional;

public interface ComponentHolder
{
    default <T1 extends LivingMachineComponent> Optional<With1<T1>> withComponents(ComponentType<T1> c1)
    {
        Collection<T1> t1 = getComponent(c1);

        if (t1 == null)
            return Optional.empty();

        return Optional.of(new With1<>(t1));
    }

    default  <T1 extends LivingMachineComponent, T2 extends LivingMachineComponent> Optional<With2<T1, T2>> withComponents(ComponentType<T1> c1, ComponentType<T2> c2)
    {
        Collection<T1> t1 = getComponent(c1);
        Collection<T2> t2 = getComponent(c2);

        if (t1 == null || t2 == null)
            return Optional.empty();

        return Optional.of(new With2<>(t1, t2));
    }

    <T extends LivingMachineComponent> Collection<T> getComponent(ComponentType<T> type);

    default  <T1 extends LivingMachineComponent, T2 extends LivingMachineComponent, T3 extends LivingMachineComponent> Optional<With3<T1, T2, T3>> withComponents(ComponentType<T1> c1, ComponentType<T2> c2, ComponentType<T3> c3)
    {
        Collection<T1> t1 = getComponent(c1);
        Collection<T2> t2 = getComponent(c2);
        Collection<T3> t3 = getComponent(c3);

        if (t1 == null || t2 == null || t3 == null)
            return Optional.empty();

        return Optional.of(new With3<>(t1, t2, t3));
    }

    default  <T1 extends LivingMachineComponent, T2 extends LivingMachineComponent, T3 extends LivingMachineComponent, T4 extends LivingMachineComponent> Optional<With4<T1, T2, T3, T4>> withComponents(ComponentType<T1> c1, ComponentType<T2> c2, ComponentType<T3> c3, ComponentType<T4> c4)
    {
        Collection<T1> t1 = getComponent(c1);
        Collection<T2> t2 = getComponent(c2);
        Collection<T3> t3 = getComponent(c3);
        Collection<T4> t4 = getComponent(c4);

        if (t1 == null || t2 == null || t3 == null || t4 == null)
            return Optional.empty();

        return Optional.of(new With4<>(t1, t2, t3, t4));
    }

//    interface Result<T>
//    {
//        void consume(Consumer<T> consumer);
//
//        boolean isPresent();
//
//    }
//
//    class EmptyResult<T> implements Result<T>
//    {
//        private Empt
//
//        @Override
//        public void consume(Consumer<T> consumer)
//        {
//
//        }
//
//        @Override
//        public boolean isPresent()
//        {
//            return false;
//        }
//    }
//
//    class FullResult<T> implements Result<T>
//    {
//        private final T entry;
//
//        public FullResult(T entry)
//        {
//            this.entry = entry;
//        }
//
//        public void consume(Consumer<T> consumer)
//        {
//            consumer.accept(entry);
//        }
//
//        @Override
//        public boolean isPresent()
//        {
//            return true;
//        }
//    }

    record With1<T1>(Collection<T1> t1) { }
    record With2<T1, T2>(Collection<T1> t1, Collection<T2> t2) { }
    record With3<T1, T2, T3>(Collection<T1> t1, Collection<T2> t2, Collection<T3> t3) { }
    record With4<T1, T2, T3, T4>(Collection<T1> t1, Collection<T2> t2, Collection<T3> t3, Collection<T4> t4) { }
}
