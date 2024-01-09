package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Merge two iterables together and iterate through them one by one.
 * Alternative of {@link com.google.common.collect.Iterables#concat(Iterable, Iterable)}
 **/
@SuppressWarnings("unused")
public class MergedIterable<T> implements Iterable<T> {
    private final Iterable<T> iterable1;
    private final Iterable<T> iterable2;

    /**
     * @param iterable1 The first iterable to be merged
     * @param iterable2 The second iterable to be merged
     **/
    public MergedIterable(@NotNull Iterable<T> iterable1, @NotNull Iterable<T> iterable2) {
        this.iterable1 = iterable1;
        this.iterable2 = iterable2;
    }

    /**
     * @param iterator1 The first iterator to be merged
     * @param iterator2 The second iterator to be merged
     **/
    public MergedIterable(@NotNull Iterator<T> iterator1, @NotNull Iterator<T> iterator2) {
        this.iterable1 = new Iterable<T>() {
            public @NotNull Iterator<T> iterator() {
                return iterator1;
            }
        };
        this.iterable2 = new Iterable<T>() {
            public @NotNull Iterator<T> iterator() {
                return iterator2;
            }
        };
    }
    
    public @NotNull MergedIterator<T> iterator() {
        return new MergedIterator<>(this.iterable1, this.iterable2);
    }
}