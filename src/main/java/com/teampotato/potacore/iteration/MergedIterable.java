package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Merge two iterables together and iterate through them one by one.
 * Alternative of {@link com.google.common.collect.Iterables#concat(Iterable, Iterable)}
 **/
@SuppressWarnings("unused")
public class MergedIterable<T> implements Iterable<T> {
    private final Iterator<T> iterator1;
    private final Iterator<T> iterator2;

    /**
     * @param iterable1 The first iterable to be merged
     * @param iterable2 The second iterable to be merged
     **/
    public MergedIterable(@NotNull Iterable<T> iterable1, @NotNull Iterable<T> iterable2) {
        this(iterable1.iterator(), iterable2.iterator());
    }

    /**
     * @param iterator1 The first iterator to be merged
     * @param iterator2 The second iterator to be merged
     **/
    public MergedIterable(@NotNull Iterator<T> iterator1, @NotNull Iterator<T> iterator2) {
        this.iterator1 = iterator1;
        this.iterator2 = iterator2;
    }
    
    public @NotNull MergedIterator<T> iterator() {
        return new MergedIterator<>(this.iterator1, this.iterator2);
    }

    public void forEach(@NotNull Consumer<? super T> action) {
        this.iterator().forEachRemaining(action);
    }
}