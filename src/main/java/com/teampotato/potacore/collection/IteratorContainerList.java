package com.teampotato.potacore.collection;

import com.teampotato.potacore.iteration.MergedIterable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Avoid iteration during the initialization but validate it before methods are used.
 * Or never iterating if you are not using methods that call {@link ValidatableList#validateContainer()}
 **/
@SuppressWarnings("unused")
public class IteratorContainerList<G> extends ValidatableList<G> {
    /**
     * @param contained     The iterable to be contained
     * @param containerType should be an empty set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerList(@NotNull Iterable<G> contained, @NotNull List<G> containerType) {
        super(contained, containerType);
    }

    /**
     * @param contained     The iterator to be contained
     * @param containerType should be an empty set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerList(@NotNull Iterator<G> contained, @NotNull List<G> containerType) {
        super(contained, containerType);
    }

    @Override
    @NotNull
    public Iterator<G> iterator() {
        if (this.validated) {
            return this.container.iterator();
        } else {
            return this.iteratorSource.iterator();
        }
    }
    
    @Override
    public boolean isEmpty() {
        if (this.validated) {
            return this.container.isEmpty();
        } else {
            return !this.iteratorSource.iterator().hasNext();
        }
    }

    @Override
    public boolean add(G g) {
        if (this.validated) {
            return this.container.add(g);
        } else {
            Iterator<G> iterator = this.iteratorSource.iterator();
            this.iteratorSource = new MergedIterable<>(iterator, Collections.singleton(g).iterator());
            return true;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(@NotNull Collection<? extends G> c) {
        if (this.validated) {
            return this.container.addAll(c);
        } else {
            Iterator<G> iterator = this.iteratorSource.iterator();
            this.iteratorSource = new MergedIterable<>(iterator, (Iterator<G>) c.iterator());
            return true;
        }
    }
}
