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
public class FilterableIterator<K> implements Iterator<K> {
    private final Predicate<K> filter;
    private final Iterator<K> iterator;
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
        this.iterator.remove();
    }
}
