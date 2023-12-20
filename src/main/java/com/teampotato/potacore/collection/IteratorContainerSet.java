package com.teampotato.potacore.collection;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@ThreadSafe
@SuppressWarnings("unused")
public class IteratorContainerSet<E> implements Set<E> {
    private final AtomicReference<Iterable<E>> iterable = new AtomicReference<>();
    private final AtomicBoolean validated = new AtomicBoolean();
    private final Set<E> set;

    public IteratorContainerSet(@NotNull Iterable<E> iterable) {
        this.iterable.set(iterable);
        this.set = new ObjectOpenHashSet<>();
    }

    private void validateSet() {
        if (this.validated.get()) return;
        this.validated.set(true);
        synchronized (this.set) {
            if (!this.set.isEmpty()) throw new ConcurrentModificationException("The set field in IteratorContainerSet cannot be modified before validation.");
            this.iterable.get().forEach(this.set::add);
            this.iterable.set(null);
        }
    }

    @Override
    public int size() {
        this.validateSet();
        synchronized (this.set) {
            return this.set.size();
        }
    }

    @Override
    public boolean isEmpty() {
        if (this.validated.get()) {
            synchronized (this.set) {
                return this.set.isEmpty();
            }
        } else {
            return this.iterable.get().iterator().hasNext();
        }
    }

    @Override
    public boolean contains(Object o) {
        this.validateSet();
        synchronized (this.set) {
            return this.set.contains(o);
        }
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        if (this.validated.get()) {
            synchronized (this.set) {
                return this.set.iterator();
            }
        } else {
            return this.iterable.get().iterator();
        }
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        this.validateSet();
        synchronized (this.set) {
            return this.set.toArray();
        }
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        this.validateSet();
        synchronized (this.set) {
            return this.set.toArray(a);
        }
    }

    @Override
    public boolean add(E e) {
        this.validateSet();
        synchronized (this.set) {
            return this.set.add(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        this.validateSet();
        synchronized (this.set) {
            return this.set.remove(o);
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        this.validateSet();
        synchronized (this.set) {
            return this.set.containsAll(c);
        }
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        this.validateSet();
        synchronized (this.set) {
            return this.set.addAll(c);
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        this.validateSet();
        synchronized (this.set) {
            return this.set.removeAll(c);
        }
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        this.validateSet();
        synchronized (this.set) {
            return this.set.retainAll(c);
        }
    }

    @Override
    public void clear() {
        this.iterable.set(null);
        synchronized (this.set) {
            this.set.clear();
        }
    }

    @Override
    public Spliterator<E> spliterator() {
        if (this.validated.get()) {
            synchronized (this.set) {
                return this.set.spliterator();
            }
        } else {
            return this.iterable.get().spliterator();
        }
    }
}
