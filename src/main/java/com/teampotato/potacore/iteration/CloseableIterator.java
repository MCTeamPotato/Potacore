package com.teampotato.potacore.iteration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
    static void close(@Nullable Iterator<?> iterator) {
        if (iterator == null) return;
        try {
            ((AutoCloseable)iterator).close();
        } catch (Exception exception) {
            if (exception instanceof ClassCastException) return;
            LOGGER.warn("Error occurs during CloseableIterator closing", exception);
        }
    }

    static void close(final @Nullable Iterator<?>... iterators) {
        if (iterators == null) return;
        for (Iterator<?> iterator : iterators) {
            close(iterator);
        }
    }

    Logger LOGGER = LogManager.getLogger(CloseableIterator.class);
}

