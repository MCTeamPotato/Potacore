package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@SuppressWarnings("unused")
public class MergedIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator1;
    private final Iterator<T> iterator2;
    private boolean useIterator1;

    public MergedIterator(@NotNull Iterator<T> iterator1, @NotNull Iterator<T> iterator2) {
        this.iterator1 = iterator1;
        this.iterator2 = iterator2;
        this.useIterator1 = true;
    }

    public MergedIterator(@NotNull Iterable<T> iterable1, @NotNull Iterable<T> iterable2) {
        this(iterable1.iterator(), iterable2.iterator());
    }

    public boolean hasNext() {
        return (this.isUseIterator1() && this.iterator1.hasNext()) || this.iterator2.hasNext();
    }

    public T next() {
        if (this.isUseIterator1()) {
            if (this.iterator1.hasNext()) {
                return this.iterator1.next();
            } else {
                this.useIterator1 = false;
            }
        }
        return this.iterator2.next();
    }

    public void remove() {
        if (this.isUseIterator1()) {
            this.iterator1.remove();
        } else {
            this.iterator2.remove();
        }
    }

    public boolean isUseIterator1() {
        return this.useIterator1;
    }
}
