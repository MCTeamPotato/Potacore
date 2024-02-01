package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Merge two iterators together and iterate through them one by one.
 * Alternative of {@link com.google.common.collect.Iterators#concat(Iterator, Iterator)}
 **/
@SuppressWarnings("unused")
public class MergedIterator<T> implements Iterator<T> {
    private final Iterator<T> iterator1;
    private final Iterator<T> iterator2;
    private boolean useIterator1;

    /**
     * @param iterator1 The first iterator to be merged
     * @param iterator2 The second iterator to be merged
     **/
    public MergedIterator(@NotNull Iterator<T> iterator1, @NotNull Iterator<T> iterator2) {
        this.iterator1 = iterator1;
        this.iterator2 = iterator2;
        this.useIterator1 = true;
    }

    /**
     * @param iterable1 The first iterable to be merged
     * @param iterable2 The second iterable to be merged
     **/
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

    /**
     * @return whether or not this merged iterator is using the first iterator
     **/
    public boolean isUseIterator1() {
        return this.useIterator1;
    }
}
