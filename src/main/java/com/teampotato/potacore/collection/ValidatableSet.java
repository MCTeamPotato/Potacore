package com.teampotato.potacore.collection;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ApiStatus.Internal
class ValidatableSet<B> implements Set<B> {
    final Set<B> container;
    volatile Iterable<B> iteratorSource = null;
    volatile boolean validated;

    public ValidatableSet(@NotNull Iterable<B> contained, @NotNull Set<B> containerType) {
        this(contained.iterator(), containerType);
    }

    public ValidatableSet(@NotNull Iterator<B> contained, @NotNull Set<B> containerType) {
        if (!containerType.isEmpty()) throw new UnsupportedOperationException("containerType is excepted to be empty");
        this.container = containerType;
        this.iteratorSource = () -> contained;
    }

    /**
     * Useful before validation
     * @return the internal unmodifiable iterator
     **/
    @NotNull
    public Iterator<B> unmodifiableIterator() {
        if (this.validated) {
            return Iterators.unmodifiableIterator(this.container.iterator());
        } else {
            return Iterators.unmodifiableIterator(this.iteratorSource.iterator());
        }
    }

    void validateContainer() {
        if (this.validated) return;
        this.validated = true;
        synchronized (this.container) {
            this.iteratorSource.forEach(this.container::add);
        }
        this.iteratorSource = null;
    }

    public int size() {
        this.validateContainer();
        return this.container.size();
    }

    public boolean isEmpty() {
        this.validateContainer();
        return this.container.isEmpty();
    }

    public boolean contains(Object o) {
        this.validateContainer();
        return this.container.contains(o);
    }

    @NotNull
    public Iterator<B> iterator() {
        this.validateContainer();
        return this.container.iterator();
    }

    public void forEach(Consumer<? super B> action) {
        this.validateContainer();
        this.container.forEach(action);
    }

    public Object @NotNull [] toArray() {
        this.validateContainer();
        return this.container.toArray();
    }

    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        this.validateContainer();
        return this.container.toArray(a);
    }

    public boolean add(B b) {
        this.validateContainer();
        return this.container.add(b);
    }

    public boolean remove(Object o) {
        this.validateContainer();
        return this.container.remove(o);
    }

    public boolean containsAll(@NotNull Collection<?> c) {
        this.validateContainer();
        return new HashSet<>(this.container).containsAll(c);
    }

    public boolean addAll(@NotNull Collection<? extends B> c) {
        this.validateContainer();
        return this.container.addAll(c);
    }

    public boolean removeAll(@NotNull Collection<?> c) {
        this.validateContainer();
        return this.container.removeAll(c);
    }

    public boolean removeIf(Predicate<? super B> filter) {
        this.validateContainer();
        return this.container.removeIf(filter);
    }

    public boolean retainAll(@NotNull Collection<?> c) {
        this.validateContainer();
        return this.container.retainAll(c);
    }

    public void clear() {
        this.validateContainer();
        this.container.clear();
    }


    public Spliterator<B> spliterator() {
        this.validateContainer();
        return this.container.spliterator();
    }
}
