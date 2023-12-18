package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@SuppressWarnings("unused")
public class MergedIterable<T> implements Iterable<T> {
    public final Iterable<T> iterable1;
    public final Iterable<T> iterable2;

    public MergedIterable(@NotNull Iterable<T> iterable1, @NotNull Iterable<T> iterable2) {
        this.iterable1 = iterable1;
        this.iterable2 = iterable2;
    }

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

    @Override
    public @NotNull MergedIterator<T> iterator() {
        return new MergedIterator<>(this.iterable1, this.iterable2);
    }
}