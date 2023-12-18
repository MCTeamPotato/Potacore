package com.teampotato.potacore.collection;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class IteratorContainerList<E> implements List<E> {
    public Iterator<E> iterator;
    public Iterable<E> iteratorCopySource;

    public final ObjectArrayList<E> iteratorList = new ObjectArrayList<>();

    public volatile boolean listValidated;

    public IteratorContainerList(@NotNull Iterable<E> iterable) {
        this(iterable.iterator());
    }

    public IteratorContainerList(@NotNull Iterator<E> iterator) {
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
        if (this.listValidated) return Spliterators.spliterator(this.iteratorList, Spliterator.ORDERED);
        return Spliterators.spliteratorUnknownSize(this.iterator(), 0);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        if (!this.listValidated) {
            while (this.iterator.hasNext()) action.accept(this.iterator.next());
        } else {
            this.iteratorList.forEach(action);
        }
    }

    public void validateList() {
        if (this.listValidated) return;
        this.listValidated = true;
        synchronized (this.iteratorList) {
            while (this.iterator.hasNext()) {
                this.iteratorList.add(this.iterator.next());
            }
        }
        this.iterator = Collections.emptyIterator();
        this.iteratorCopySource = Collections::emptyIterator;
    }

    public static <T> IteratorContainerList<T> cast(List<T> list) {
        return (IteratorContainerList<T>) list;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        if (this.listValidated) return this.iteratorList.iterator();
        return this.iteratorCopySource.iterator();
    }

    @Override
    public boolean isEmpty() {
        if (this.listValidated) return this.iteratorList.isEmpty();
        return this.iterator.hasNext();
    }

    @Override
    public void clear() {
        this.iteratorList.clear();
        this.iterator = Collections.emptyIterator();
        this.iteratorCopySource = Collections::emptyIterator;
    }

    @Override
    public E get(int index) {
        this.validateList();
        return this.iteratorList.get(index);
    }

    @Override
    public E set(int index, E element) {
        this.validateList();
        return this.iteratorList.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        this.validateList();
        this.iteratorList.add(index, element);
    }

    @Override
    public E remove(int index) {
        this.validateList();
        return this.iteratorList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        this.validateList();
        return this.iteratorList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        this.validateList();
        return this.iteratorList.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        this.validateList();
        return this.iteratorList.listIterator();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        this.validateList();
        return this.iteratorList.listIterator(index);
    }

    @NotNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        this.validateList();
        return this.iteratorList.subList(fromIndex, toIndex);
    }

    @Override
    public int size() {
        this.validateList();
        return this.iteratorList.size();
    }

    @Override
    public boolean contains(Object o) {
        this.validateList();
        return this.iteratorList.contains(o);
    }


    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        this.validateList();
        return this.iteratorList.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(T @NotNull[] a) {
        this.validateList();
        return this.iteratorList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        this.validateList();
        return this.iteratorList.add(e);
    }

    @Override
    public boolean remove(Object o) {
        this.validateList();
        return this.iteratorList.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        this.validateList();
        return this.iteratorList.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        this.validateList();
        return this.iteratorList.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        this.validateList();
        return this.iteratorList.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        this.validateList();
        return this.iteratorList.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        this.validateList();
        return this.iteratorList.retainAll(c);
    }
}
