package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class MergedIterable<T> implements Iterable<T> {
    private final Iterator<T> iterator1;
    private final Iterator<T> iterator2;

    public MergedIterable(@NotNull Iterable<T> iterable1, @NotNull Iterable<T> iterable2) {
        this(iterable1.iterator(), iterable2.iterator());
    }

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