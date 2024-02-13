package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Alternative of {@link com.google.common.collect.Iterators#filter(Iterator, com.google.common.base.Predicate)}
 * @param <K> generics object
 **/
public class FilterableIterator<K> implements CloseableIterator<K> {
    private @Nullable Predicate<K> filter;
    private @Nullable Iterator<K> iterator;
    private @Nullable K next;

    /**
     * @param iterator The iterator to be filtered
     * @param filter The filter
     **/
    public FilterableIterator(@NotNull Iterator<K> iterator, @NotNull Predicate<K> filter) {
        this.filter = filter;
        this.iterator = iterator;
    }

    private void advance() {
        if (this.iterator == null || this.filter == null) return;
        while (true) {
            if (this.iterator.hasNext()) {
                final K candidate = this.iterator.next();
                if (this.filter.test(candidate)) {
                    this.next = candidate;
                    break;
                } else {
                    this.next = null;
                }
            } else {
                this.next = null;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        this.advance();
        return this.next != null;
    }

    @Override
    public K next() {
        if (this.next == null) throw new NoSuchElementException();
        return this.next;
    }

    @Override
    public void forEachRemaining(@NotNull Consumer<? super K> action) {
        while (this.hasNext()) action.accept(this.next());
    }

    @Override
    public void remove() {
        if (this.iterator == null) return;
        this.iterator.remove();
    }

    @Override
    public void close() {
        CloseableIterator.close(this.iterator);
        this.next = null;
        this.iterator = null;
        this.filter = null;
    }
}
