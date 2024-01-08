package com.teampotato.potacore.collection;

import com.google.common.collect.Iterators;
import com.teampotato.potacore.iteration.MergedIterable;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

/**
 * Avoid iteration during the initialization but validate it before methods are used.
 * Or never iterating if you are not using methods that call {@link IteratorContainerSet#validateContainer()}
 **/
@SuppressWarnings("unused")
public class IteratorContainerSet<G> implements Set<G> {
    private final AtomicReference<Iterable<G>> iteratorCopySource = new AtomicReference<>();
    private final Set<G> container;
    private final AtomicBoolean validated = new AtomicBoolean(false);
    private @Nullable Set<G> containCheckHelper;
    private final AtomicBoolean allowCacheOnContainsCheck = new AtomicBoolean(false);

    /**
     * @param iterable The iterable to be contained
     * @param internalContainerType should be an empty (or {@link UnsupportedOperationException UnsupportedOperationException} will be thrown when you use it) set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     * @param allowCacheOnContainsCheck If enabled, a {@link ObjectOpenHashSet} will be initialized when you firstly call {@link IteratorContainerList#contains(Object)} or {@link IteratorContainerList#containsAll(Collection)} for faster contains check. If you are not using {@link it.unimi.dsi.fastutil.objects.ObjectArraySet}-like container, you can keep this false.
     **/
    public IteratorContainerSet(@NotNull Iterable<G> iterable, @NotNull Set<G> internalContainerType, boolean allowCacheOnContainsCheck) {
        this.iteratorCopySource.set(iterable);
        this.container = internalContainerType;
        this.allowCacheOnContainsCheck.set(allowCacheOnContainsCheck);
    }

    /**
     * @param iterator The iterator to be contained
     * @param internalContainerType should be an empty (or {@link UnsupportedOperationException UnsupportedOperationException} will be thrown when you use it) set, for example {@link it.unimi.dsi.fastutil.objects.ObjectOpenHashSet} or {@link it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet}
     * @param allowCacheOnContainsCheck If enabled, a {@link ObjectOpenHashSet} will be initialized when you firstly call {@link IteratorContainerList#contains(Object)} or {@link IteratorContainerList#containsAll(Collection)} for faster contains check. If you are not using {@link it.unimi.dsi.fastutil.objects.ObjectArraySet}-like container, you can keep this false.
     **/
    public IteratorContainerSet(Iterator<G> iterator, @NotNull Set<G> internalContainerType, boolean allowCacheOnContainsCheck) {
        this.iteratorCopySource.set(new Iterable<G>() {
            public @NotNull Iterator<G> iterator() {
                return iterator;
            }
        });
        this.container = internalContainerType;
        this.allowCacheOnContainsCheck.set(allowCacheOnContainsCheck);
    }

    private void validateContainer() {
        if (this.validated.get()) return;
        this.validated.set(true);
        synchronized (this.container) {
            if (this.iteratorCopySource.get() == null) throw new NullPointerException("Already validated");
            if (!this.container.isEmpty()) throw new UnsupportedOperationException("Container must be empty before validation");
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
            return !this.iteratorCopySource.get().iterator().hasNext();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (this.allowCacheOnContainsCheck.get()) {
            if (this.containCheckHelper == null) {
                if (!this.validated.get()) {
                    this.containCheckHelper = new ObjectOpenHashSet<>(this.iteratorCopySource.get().iterator());
                } else {
                    synchronized (this.container) {
                        this.containCheckHelper = new ObjectOpenHashSet<>(this.container);
                    }
                }
            }
            return this.containCheckHelper.contains(o);
        } else {
            this.validateContainer();
            synchronized (this.container) {
                return this.container.contains(o);
            }
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
                this.validated.set(true);
                this.iteratorCopySource.set(null);
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
        if (this.allowCacheOnContainsCheck.get()) {
            if (this.containCheckHelper == null) {
                if (!this.validated.get()) {
                    this.containCheckHelper = new ObjectOpenHashSet<>(this.iteratorCopySource.get().iterator());
                } else {
                    synchronized (this.container) {
                        this.containCheckHelper = new ObjectOpenHashSet<>(this.container);
                    }
                }
            }
            return this.containCheckHelper.containsAll(c);
        } else {
            this.validateContainer();
            synchronized (this.container) {
                return new ObjectOpenHashSet<>(this.container).containsAll(c);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(@NotNull Collection<? extends G> c) {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.addAll(c);
            }
        } else {
            Iterator<G> iterator = this.iteratorCopySource.get().iterator();
            Iterator<G> cIterator = (Iterator<G>) c.iterator();
            this.iteratorCopySource.set(new MergedIterable<>(cIterator, iterator));
            return true;
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
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.removeIf(filter);
            }
        } else {
            Iterator<G> iterator = this.iteratorCopySource.get().iterator();
            this.iteratorCopySource.set(() -> Iterators.filter(iterator, obj -> !filter.test(obj)));
            return true;
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
}
