package com.teampotato.potacore.collection;

import com.google.common.collect.Iterators;
import com.teampotato.potacore.iteration.MergedIterable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Avoid iteration during the initialization but validate it before methods are used.
 * Or never iterating if you are not using methods that call {@link IteratorContainerSet#validateContainer()}
 **/
@SuppressWarnings("unused")
public class IteratorContainerSet<G> implements Set<G> {

    private final Set<G> container;
    private final AtomicReference<Iterable<G>> iteratorSource = new AtomicReference<>();
    private final AtomicBoolean validated = new AtomicBoolean();

    /**
     * @param contained The iterable to be contained
     * @param containerType should be an empty set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerSet(@NotNull Iterable<G> contained, @NotNull Set<G> containerType) {
        if (!containerType.isEmpty()) throw new UnsupportedOperationException("containerType is excepted to be empty");
        this.container = containerType;
        this.iteratorSource.set(contained);
    }

    /**
     * @param contained The iterator to be contained
     * @param containerType should be an empty set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerSet(@NotNull Iterator<G> contained, @NotNull Set<G> containerType) {
        this(() -> contained, containerType);
    }

    private void validateContainer() {
        if (this.validated.get()) return;
        this.validated.set(true);
        synchronized (this.container) {
            this.iteratorSource.get().forEach(this.container::add);
        }
        this.iteratorSource.set(null);
    }
    
    public int size() {
        if (this.isEmpty()) return 0;
        this.validateContainer();
        synchronized (this.container) {
            return this.container.size();
        }
    }

    public boolean isEmpty() {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.isEmpty();
            }
        } else {
            return this.iteratorSource.get().iterator().hasNext();
        }
    }

    public boolean contains(Object o) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.contains(o);
        }
    }

    @NotNull
    public Iterator<G> iterator() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.iterator();
        }
    }

    public void forEach(Consumer<? super G> action) {
        if (this.validated.get()) {
            synchronized (this.container) {
                this.container.forEach(action);
            }
        } else {
            this.iteratorSource.get().forEach(obj -> {
                if (this.container.add(obj)) action.accept(obj);
            });
            this.validated.set(true);
            this.iteratorSource.set(null);
        }
    }
    
    public Object @NotNull [] toArray() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.toArray();
        }
    }
    
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.toArray(a);
        }
    }
    public boolean add(G g) {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.remove(g);
            }
        } else {
            Collection<G> c = Collections.singleton(g);
            Iterator<G> iterator = this.iteratorSource.get().iterator();
            Iterator<G> collectionIterator = c.iterator();
            this.iteratorSource.set(new MergedIterable<>(iterator, collectionIterator));
            return true;
        }
    }

    public boolean remove(Object o) {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.remove(o);
            }
        } else {
            Iterator<G> iterator = Iterators.filter(this.iteratorSource.get().iterator(), obj -> obj != o);
            this.iteratorSource.set(() -> iterator);
            return true;
        }
    }
    
    public boolean containsAll(@NotNull Collection<?> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.containsAll(c);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean addAll(@NotNull Collection<? extends G> c) {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.addAll(c);
            }
        } else {
            Iterator<G> iterator = this.iteratorSource.get().iterator();
            Iterator<G> collectionIterator = (Iterator<G>) c.iterator();
            this.iteratorSource.set(new MergedIterable<>(iterator, collectionIterator));
            return true;
        }
    }

    public boolean retainAll(@NotNull Collection<?> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.retainAll(c);
        }
    }
    
    public boolean removeAll(@NotNull Collection<?> c) {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.removeAll(c);
            }
        } else {
            Set<?> set;
            if (c instanceof Set) {
                set = (Set<?>) c;
            } else {
                set = new IteratorContainerSet<>(c, new HashSet<>());
            }
            Iterator<G> iterator = Iterators.filter(this.iteratorSource.get().iterator(), obj -> !set.contains(obj));
            this.iteratorSource.set(() -> iterator);
            return true;
        }
    }

    public boolean removeIf(Predicate<? super G> filter) {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.removeIf(filter);
            }
        } else {
            Iterator<G> iterator = Iterators.filter(this.iteratorSource.get().iterator(), obj -> !filter.test(obj));
            this.iteratorSource.set(() -> iterator);
            return true;
        }
    }
    
    public void clear() {
        if (this.validated.get()) {
            synchronized (this.container) {
                this.container.clear();
            }
        } else {
            this.iteratorSource.set(null);
        }
    }

    public Spliterator<G> spliterator() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.spliterator();
        }
    }
}
