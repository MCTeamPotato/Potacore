package com.teampotato.potacore.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

/**
 * Avoid iteration during the initialization but validate it before methods are used.
 * Or never iterating if you are not using methods that call {@link ValidatableList#validateContainer()}
 **/
@SuppressWarnings("unused")
public class IteratorContainerList<G> extends ValidatableList<G> {
    /**
     * @param contained The iterable to be contained
     * @param containerType should be an empty set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerList(@NotNull Iterable<G> contained, @NotNull List<G> containerType) {
        super(contained, containerType);
    }

    /**
     * @param contained The iterator to be contained
     * @param containerType should be an empty set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerList(@NotNull Iterator<G> contained, @NotNull List<G> containerType) {
        super(contained, containerType);
    }
}
