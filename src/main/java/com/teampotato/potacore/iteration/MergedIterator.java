package com.teampotato.potacore.iteration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

@SuppressWarnings("unused")
public class MergedIterator<T> implements Iterator<T> {
    private final List<Iterator<T>> iteratorList = new ArrayList<>();
    private int currentIndex = 0;

    @SafeVarargs
    public MergedIterator(Iterable<T> @NotNull ... iterables) {
        for (Iterable<T> iterable : iterables) {
            iteratorList.add(iterable.iterator());
        }
    }

    @Override
    public boolean hasNext() {
        while (currentIndex < iteratorList.size()) {
            if (iteratorList.get(currentIndex).hasNext()) return true;
            currentIndex++;
        }
        return false;
    }

    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        return iteratorList.get(currentIndex).next();
    }

    @Override
    public void remove() {
        iteratorList.get(currentIndex).remove();
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }
}