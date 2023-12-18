package com.teampotato.potacore.iteration;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class IteratorContainerSet<E> implements Set<E>, Iterable<E> {
    public Iterator<E> iterator;
    public Iterable<E> iteratorCopySource;

    public final Set<E> iteratorSet = new ObjectOpenHashSet<>();

    public volatile boolean setValidated;

    public IteratorContainerSet(@NotNull Iterable<E> iterable) {
        Iterator<E> iterator = iterable.iterator();
        this.iterator = iterator;
        this.iteratorCopySource = new Iterable<E>() {
            public @NotNull Iterator<E> iterator() {
                return iterator;
            }
        };
    }

    public IteratorContainerSet(@NotNull Iterator<E> iterator) {
        this.iterator = iterator;
        this.iteratorCopySource = new Iterable<E>() {
            public @NotNull Iterator<E> iterator() {
                return iterator;
            }
        };
    }

    public boolean hasNext() {
        if (this.setValidated) throw new UnsupportedOperationException();
        return this.iterator.hasNext();
    }

    public E next() {
        if (this.setValidated) throw new UnsupportedOperationException();
        return this.iterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public Spliterator<E> spliterator() {
        return Spliterators.spliteratorUnknownSize(this.iterator(), 0);
    }

    public void forEach(Consumer<? super E> action) {
        if (!this.setValidated) {
            while (this.hasNext()) action.accept(this.next());
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
