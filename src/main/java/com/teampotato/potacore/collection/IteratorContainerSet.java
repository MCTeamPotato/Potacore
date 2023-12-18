package com.teampotato.potacore.collection;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class IteratorContainerSet<E> implements Set<E> {
    public Iterator<E> iterator;
    public Iterable<E> iteratorCopySource;

    public final ObjectOpenHashSet<E> iteratorSet = new ObjectOpenHashSet<>();

    public volatile boolean setValidated;

    public IteratorContainerSet(@NotNull Iterable<E> iterable) {
        this(iterable.iterator());
    }

    public IteratorContainerSet(@NotNull Iterator<E> iterator) {
        this.iterator = iterator;
        this.iteratorCopySource = new Iterable<E>() {
            @Override
            public @NotNull Iterator<E> iterator() {
                return iterator;
            }
        };
    }

    @Override
    public Spliterator<E> spliterator() {
        if (this.setValidated) return Spliterators.spliterator(this.iteratorSet, Spliterator.DISTINCT);
        return Spliterators.spliteratorUnknownSize(this.iterator(), 0);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        if (!this.setValidated) {
            while (this.iterator.hasNext()) action.accept(this.iterator.next());
        } else {
            this.iteratorSet.forEach(action);
        }
    }

    public void validateSet() {
        if (this.setValidated) return;
        this.setValidated = true;
        synchronized (this.iteratorSet) {
            while (this.iterator.hasNext()) {
                this.iteratorSet.add(this.iterator.next());
            }
        }
        this.iterator = Collections.emptyIterator();
        this.iteratorCopySource = Collections::emptyIterator;
    }

    public static <T> IteratorContainerSet<T> cast(Set<T> set) {
        return (IteratorContainerSet<T>) set;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        if (this.setValidated) return this.iteratorSet.iterator();
        return this.iteratorCopySource.iterator();
    }

    @Override
    public boolean isEmpty() {
        if (this.setValidated) return this.iteratorSet.isEmpty();
        return this.iterator.hasNext();
    }

    @Override
    public void clear() {
        this.iteratorSet.clear();
        this.iterator = Collections.emptyIterator();
        this.iteratorCopySource = Collections::emptyIterator;
    }

    @Override
    public int size() {
        this.validateSet();
        return this.iteratorSet.size();
    }

    @Override
    public boolean contains(Object o) {
        this.validateSet();
        return this.iteratorSet.contains(o);
    }


    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        this.validateSet();
        return this.iteratorSet.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(T @NotNull[] a) {
        this.validateSet();
        return this.iteratorSet.toArray(a);
    }

    @Override
    public boolean add(E e) {
        this.validateSet();
        return this.iteratorSet.add(e);
    }

    @Override
    public boolean remove(Object o) {
        this.validateSet();
        return this.iteratorSet.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        this.validateSet();
        return this.iteratorSet.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        this.validateSet();
        return this.iteratorSet.addAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        this.validateSet();
        return this.iteratorSet.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        this.validateSet();
        return this.iteratorSet.retainAll(c);
    }
}