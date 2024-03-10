package com.teampotato.potacore.iteration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/**
 * Interface to concat {@link Iterator} and {@link AutoCloseable}
 **/
public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
    /**
     * Close a iterator if it is closeable.
     * @param iterator The iterator to be closed
     **/
    static void close(@Nullable Iterator<?> iterator) {
        if (iterator == null) return;
        try {
            ((AutoCloseable)iterator).close();
        } catch (Exception exception) {
            if (exception instanceof ClassCastException) return;
            LOGGER.warn("Error occurs during CloseableIterator closing", exception);
        }
    }

    /**
     * Close iterators if they're closeable.
     * @param iterators The iterators to be closed
     **/
    @SuppressWarnings("ForLoopReplaceableByForEach") // for i loop is a bit more performant
    static void close(final @Nullable Iterator<?>... iterators) {
        if (iterators == null) return;
        for (int index = 0; index < iterators.length; index++) {
            close(iterators[index]);
        }
    }

    /**
     * Closing exception logger
     **/
    Logger LOGGER = LogManager.getLogger(CloseableIterator.class);
}

