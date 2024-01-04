package com.teampotato.potacore.collection;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Avoid iteration during the initialization but validate it before methods are used.
 * Or never iterating if you are not using methods that call {@link IteratorContainerSet#validateContainer()}
 **/
@SuppressWarnings("unused")
public class IteratorContainerSet<G> implements Set<G> {
    private final AtomicReference<Iterable<G>> iteratorCopySource = new AtomicReference<>();
    private final Set<G> container;
    private final AtomicBoolean validated = new AtomicBoolean(false);
    private final AtomicBoolean isEmpty = new AtomicBoolean();

    /**
     * @param iterable The iterable to be contained
     * @param internalContainerType should be an empty (or {@link UnsupportedOperationException UnsupportedOperationException} will be thrown when you use it) set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerSet(@NotNull Iterable<G> iterable, @NotNull Set<G> internalContainerType) {
        this.iteratorCopySource.set(iterable);
        this.container = internalContainerType;
        this.isEmpty.set(!this.iteratorCopySource.get().iterator().hasNext());
    }

    /**
     * @param iterator The iterator to be contained
     * @param internalContainerType should be an empty (or {@link UnsupportedOperationException UnsupportedOperationException} will be thrown when you use it) set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     **/
    public IteratorContainerSet(Iterator<G> iterator, @NotNull Set<G> internalContainerType) {
        this.iteratorCopySource.set(new Iterable<G>() {
            public @NotNull Iterator<G> iterator() {
                return iterator;
            }
        });
        this.container = internalContainerType;
        this.isEmpty.set(!this.iteratorCopySource.get().iterator().hasNext());
    }

    private void validateContainer() {
        if (this.isEmpty.get()) return;
        if (this.validated.get()) return;
        this.validated.set(true);
        synchronized (this.container) {
            if (iteratorCopySource.get() == null) throw new NullPointerException("Already validated");
            if (!this.container.isEmpty()) throw new UnsupportedOperationException("Set cannot be modified before validation");
            this.iteratorCopySource.get().forEach(this.container::add);
        }
        this.iteratorCopySource.set(null);
    }

    @Override
    public int size() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.size();
        }
    }

    @Override
    public boolean isEmpty() {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.isEmpty();
            }
        } else {
            return this.isEmpty.get();
        }
    }

    @Override
    public boolean contains(Object o) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.contains(o);
        }
    }

    @NotNull
    @Override
    public Iterator<G> iterator() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.iterator();
        }
    }

    @Override
    public void forEach(Consumer<? super G> action) {
        if (this.validated.get()) {
            synchronized (this.container) {
                this.container.forEach(action);
            }
        } else {
            synchronized (this.container) {
                for (G next : this.iteratorCopySource.get()) {
                    if (!this.container.add(next)) continue;
                    action.accept(next);
                }
            }
        }
    }

    @Override
    public Object @NotNull [] toArray() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.toArray();
        }
    }

    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.toArray(a);
        }
    }

    @SuppressWarnings("Since15")
    public <T> T[] toArray(@NotNull IntFunction<T[]> generator) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.toArray(generator.apply(0));
        }
    }

    @Override
    public boolean add(G g) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.add(g);
        }
    }

    @Override
    public boolean remove(Object o) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.remove(o);
        }
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.containsAll(c);
        }
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends G> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.addAll(c);
        }
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.retainAll(c);
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.removeAll(c);
        }
    }

    @Override
    public boolean removeIf(Predicate<? super G> filter) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.removeIf(filter);
        }
    }

    @Override
    public void clear() {
        synchronized (this.container) {
            this.container.clear();
        }
        this.iteratorCopySource.set(null);
    }

    @Override
    public Spliterator<G> spliterator() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.spliterator();
        }
    }

    @Override
    public Stream<G> stream() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.stream();
        }
    }

    @Override
    public Stream<G> parallelStream() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.parallelStream();
        }
    }
}
