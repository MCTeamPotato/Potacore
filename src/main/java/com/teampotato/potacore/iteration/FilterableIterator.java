package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * Filterable iterator allows you to {@link FilterableIterator#addFilter(Predicate)} add predicate filter} to skip iteration of certain elements
 * @param <K> generics object
 **/
@SuppressWarnings("unused")
public class FilterableIterator<K> implements Iterator<K> {
    private final Iterator<K> iterator;
    private @Nullable Predicate<K> filter;
    private K next;
    private static final FilterableIterator<Object> EMPTY = wrap(Collections.emptyIterator());

    /**
     * @param <H> generics object
     * @return an empty filterable iterator
     **/
    @SuppressWarnings("unchecked")
    public static <H> FilterableIterator<H> empty() {
        return (FilterableIterator<H>) EMPTY;
    }

    private FilterableIterator(Iterator<K> iterator) {
        this.iterator = iterator;
    }

    /**
     * Wrap a iterator with filterable iterator for further filtering.
     * @param iterator the iterator to be wrapped
     * @param <B> generics object
     * @return the filterable iterator containing the elements of iterator
     **/
    @Contract(value = "_ -> new", pure = true)
    public static <B> @NotNull FilterableIterator<B> wrap(@NotNull Iterator<B> iterator) {
        return new FilterableIterator<>(iterator);
    }

    /**
     * Add a filter to this iterator
     * @param filter the filter to be added
     **/
    public void addFilter(Predicate<K> filter) {
        this.filter = (this.filter == null) ? filter : this.filter.and(filter);
    }

    private boolean checkNext() {
        while (this.filter != null && this.iterator.hasNext()) {
            K candidate = this.iterator.next();
            if (this.filter.test(candidate)) {
                this.next = candidate;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        return checkNext();
    }

    @Override
    public K next() {
        return next;
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }
}
