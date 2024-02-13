package com.teampotato.potacore.iteration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

/**
 * Interface to concat {@link Iterator} and {@link AutoCloseable}
 **/
public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
    /**
     * Close a iterator if it is closeable.
     * @param iterator The iterator to be closed
     **/
    static void close(Iterator<?> iterator) {
        if (iterator == null) return;
        try {
            if (iterator instanceof AutoCloseable) ((AutoCloseable)iterator).close();
        } catch (Exception exception) {
            LOGGER.warn("Error occurs during CloseableIterator closing", exception);
        }
    }

    /**
     * Closing exception logger
     **/
    Logger LOGGER = LogManager.getLogger(CloseableIterator.class);
}

