package com.teampotato.potacore.collection;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.ThreadSafe;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Avoid iteration during the initialization but validate it before methods are used.
 * Or never iterating if you are not using methods that call {@link IteratorContainerList#validateList()}
 **/
@ThreadSafe
@SuppressWarnings("unused")
public class IteratorContainerList<E> implements List<E> {
    private final AtomicReference<Iterable<E>> iterable = new AtomicReference<>();
    private @Nullable Set<E> distinctedList;

    /**
     * Whether {@link IteratorContainerList#list} is validated
     **/
    private final AtomicBoolean validated = new AtomicBoolean();
    private final List<E> list;

    /**
     * @param iterable The iterable to be contained
     **/
    public IteratorContainerList(@NotNull Iterable<E> iterable) {
        this.iterable.set(iterable);
        this.list = new ObjectArrayList<>();
    }

    private void validateList() {
        if (this.validated.get()) return;
        this.validated.set(true);
        synchronized (this.list) {
            if (!this.list.isEmpty()) throw new ConcurrentModificationException("The list field in IteratorContainerList cannot be modified before validation.");
            this.iterable.get().forEach(this.list::add);
            this.iterable.set(null);
        }
    }

    @Override
    public int size() {
        this.validateList();
        synchronized (this.list) {
            return this.list.size();
        }
    }

    @Override
    public boolean isEmpty() {
        if (this.validated.get()) {
            synchronized (this.list) {
                return this.list.isEmpty();
            }
        } else {
            return this.iterable.get().iterator().hasNext();
        }
    }

    @Override
    public boolean contains(Object o) {
        this.validateList();
        if (this.distinctedList == null) this.distinctedList = new ObjectOpenHashSet<>(this.list);
        return this.distinctedList.contains(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        if (this.validated.get()) {
            synchronized (this.list) {
                return this.list.iterator();
            }
        } else {
            return this.iterable.get().iterator();
        }
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        this.validateList();
        synchronized (this.list) {
            return this.list.toArray();
        }
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        this.validateList();
        synchronized (this.list) {
            return this.list.toArray(a);
        }
    }

    @Override
    public boolean add(E e) {
        this.validateList();
        synchronized (this.list) {
            return this.list.add(e);
        }
    }

    @Override
    public boolean remove(Object o) {
        this.validateList();
        synchronized (this.list) {
            return this.list.remove(o);
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        this.validateList();
        synchronized (this.list) {
            if (this.distinctedList == null) this.distinctedList = new ObjectOpenHashSet<>(this.list);
            return this.distinctedList.containsAll(c);
        }
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        this.validateList();
        synchronized (this.list) {
            return this.list.addAll(c);
        }
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        this.validateList();
        synchronized (this.list) {
            return this.list.addAll(index, c);
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        this.validateList();
        synchronized (this.list) {
            return this.list.removeAll(c);
        }
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        this.validateList();
        synchronized (this.list) {
            return this.list.retainAll(c);
        }
    }

    @Override
    public void clear() {
        this.iterable.set(null);
        synchronized (this.list) {
            this.list.clear();
        }
    }

    @Override
    public E get(int index) {
        this.validateList();
        synchronized (this.list) {
            return this.list.get(index);
        }
    }

    @Override
    public E set(int index, E element) {
        this.validateList();
        synchronized (this.list) {
            return this.list.set(index, element);
        }
    }

    @Override
    public void add(int index, E element) {
        this.validateList();
        synchronized (this.list) {
            this.list.add(index, element);
        }
    }

    @Override
    public E remove(int index) {
        this.validateList();
        synchronized (this.list) {
            return this.list.remove(index);
        }
    }

    @Override
    public int indexOf(Object o) {
        this.validateList();
        synchronized (this.list) {
            return this.list.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        this.validateList();
        synchronized (this.list) {
            return this.list.lastIndexOf(o);
        }
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        this.validateList();
        synchronized (this.list) {
            return this.list.listIterator();
        }
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        this.validateList();
        synchronized (this.list) {
            return this.list.listIterator(index);
        }
    }

    @NotNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        this.validateList();
        synchronized (this.list) {
            return this.list.subList(fromIndex, toIndex);
        }
    }

    @Override
    public Spliterator<E> spliterator() {
        if (this.validated.get()) {
            synchronized (this.list) {
                return this.list.spliterator();
            }
        } else {
            return this.iterable.get().spliterator();
        }
    }
}
