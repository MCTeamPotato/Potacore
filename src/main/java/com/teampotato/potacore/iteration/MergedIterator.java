package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@SuppressWarnings("unused")
public class MergedIterator<T> implements Iterator<T> {
    public final Iterator<T> iterator1;
    public final Iterator<T> iterator2;
    public boolean useIterator1;

    public MergedIterator(@NotNull Iterator<T> iterator1, @NotNull Iterator<T> iterator2) {
        this.iterator1 = iterator1;
        this.iterator2 = iterator2;
        this.useIterator1 = true;
    }

    public MergedIterator(@NotNull Iterable<T> iterable1, @NotNull Iterable<T> iterable2) {
        this.iterator1 = iterable1.iterator();
        this.iterator2 = iterable2.iterator();
        this.useIterator1 = true;
    }

    @Override
    public boolean hasNext() {
        return (this.useIterator1 && this.iterator1.hasNext()) || this.iterator2.hasNext();
    }

    @Override
    public T next() {
        if (this.useIterator1) {
            if (this.iterator1.hasNext()) {
                return this.iterator1.next();
            } else {
                this.useIterator1 = false;
            }
        }
        return this.iterator2.next();
    }
}
