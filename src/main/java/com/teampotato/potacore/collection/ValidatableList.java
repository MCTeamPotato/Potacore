package com.teampotato.potacore.collection;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@ApiStatus.Internal
class ValidatableList<B> implements List<B> {
    final List<B> container;
    volatile Iterable<B> iteratorSource;
    volatile boolean validated = false;

    public ValidatableList(@NotNull Iterable<B> contained, @NotNull List<B> containerType) {
        this(contained.iterator(), containerType);
    }

    public ValidatableList(@NotNull Iterator<B> contained, @NotNull List<B> containerType) {
        if (!containerType.isEmpty()) throw new UnsupportedOperationException("containerType is excepted to be empty");
        this.container = containerType;
        this.iteratorSource = () -> contained;
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
        if (this.isEmpty()) return 0;
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

    public boolean addAll(int index, @NotNull Collection<? extends B> c) {
        this.validateContainer();
        return this.container.addAll(index, c);
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

    public void replaceAll(UnaryOperator<B> operator) {
        this.validateContainer();
        this.container.replaceAll(operator);
    }

    public void sort(Comparator<? super B> c) {
        this.validateContainer();
        this.container.sort(c);
    }

    public void clear() {
        this.validateContainer();
        this.container.clear();
    }

    public B get(int index) {
        this.validateContainer();
        return this.container.get(index);
    }

    public B set(int index, B element) {
        this.validateContainer();
        return this.container.set(index, element);
    }

    public void add(int index, B element) {
        this.validateContainer();
        this.container.add(index, element);
    }

    public B remove(int index) {
        this.validateContainer();
        return this.container.remove(index);
    }

    public int indexOf(Object o) {
        this.validateContainer();
        return this.container.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        this.validateContainer();
        return this.container.lastIndexOf(o);
    }

    @NotNull
    public ListIterator<B> listIterator() {
        this.validateContainer();
        return this.container.listIterator();
    }

    @NotNull
    public ListIterator<B> listIterator(int index) {
        this.validateContainer();
        return this.container.listIterator(index);
    }

    @NotNull
    public List<B> subList(int fromIndex, int toIndex) {
        this.validateContainer();
        return this.container.subList(fromIndex, toIndex);
    }

    public Spliterator<B> spliterator() {
        this.validateContainer();
        return this.container.spliterator();
    }
}
