package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@SuppressWarnings("unused")
public class MergedIterable<T> implements Iterable<T> {
    private final Iterable<T>[] iterables;

    @SafeVarargs
    public MergedIterable(Iterable<T>... iterables) {
        this.iterables = iterables;
    }
    
    public @NotNull MergedIterator<T> iterator() {
        return new MergedIterator<>(this.iterables);
    }

    public void forEach(@NotNull Consumer<? super T> action) {
        this.iterator().forEachRemaining(action);
    }
}