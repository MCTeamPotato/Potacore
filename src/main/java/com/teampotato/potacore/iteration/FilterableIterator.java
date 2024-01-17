package com.teampotato.potacore.iteration;

import com.google.common.base.Suppliers;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
    private final Supplier<Boolean> isEmptyFilter = Suppliers.memoize(() -> this.filter == null);

    /**
     * @param <H> generics object
     * @return an empty filterable iterator
     **/
    @SuppressWarnings("unchecked")
    public static <H> FilterableIterator<H> empty() {
        return (FilterableIterator<H>) EMPTY;
    }

    private FilterableIterator(@NotNull Iterator<K> iterator) {
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
        return new FilterableIterator<>(Validate.notNull(iterator));
    }

    /**
     * Add a filter to this iterator
     * @param filter the filter to be added
     **/
    public void addFilter(Predicate<K> filter) {
        if (this.next != null) throw new UnsupportedOperationException("FilterableIterator#addFilter is not supported after iteration begins");
        this.filter = (this.filter == null) ? filter : this.filter.and(filter);
    }

    private boolean advance() {
        assert this.filter != null;
        while (this.iterator.hasNext()) {
            K candidate = this.iterator.next();
            if (this.filter.test(candidate)) {
                this.next = candidate;
                break;
            } else {
                this.next = null;
            }
        }
        return this.next != null;
    }

    @Override
    public boolean hasNext() {
        if (this.isEmptyFilter.get()) return this.iterator.hasNext();
        return this.advance();
    }

    @Override
    public K next() {
        if (this.isEmptyFilter.get()) return this.iterator.next();
        if (this.next == null) throw new NoSuchElementException();
        return this.next;
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }
}
