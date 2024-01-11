package com.teampotato.potacore.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

/**
 * Avoid iteration during the initialization but validate it before methods are used.
 * Or never iterating if you are not using methods that call {@link ValidatableSet#validateContainer()}
 **/
@SuppressWarnings("unused")
public class IteratorContainerSet<G> extends ValidatableSet<G> {
    /**
     * @param contained The iterable to be contained
     * @param containerType should be an empty set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerSet(@NotNull Iterable<G> contained, @NotNull Set<G> containerType) {
        super(contained, containerType);
    }

    /**
     * @param contained The iterator to be contained
     * @param containerType should be an empty set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerSet(@NotNull Iterator<G> contained, @NotNull Set<G> containerType) {
        super(contained, containerType);
    }
}
