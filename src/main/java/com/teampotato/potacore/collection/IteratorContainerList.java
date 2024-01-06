package com.teampotato.potacore.collection;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Avoid iteration during the initialization but validate it before methods are used.
 * Or never iterating if you are not using methods that call {@link IteratorContainerList#validateContainer()}
 **/
@SuppressWarnings("unused")
public class IteratorContainerList<G> implements List<G> {
    private final AtomicReference<Iterable<G>> iteratorCopySource = new AtomicReference<>();
    private final List<G> container;
    private final AtomicBoolean validated = new AtomicBoolean(false);
    private @Nullable Set<G> containCheckHelper;
    private final AtomicBoolean allowCacheOnContainsCheck = new AtomicBoolean(false);

    /**
     * @param iterable The iterable to be contained
     * @param internalContainerType should be an empty (or {@link UnsupportedOperationException UnsupportedOperationException} will be thrown when you use it) list, for example {@link it.unimi.dsi.fastutil.objects.ObjectArrayList} or {@link it.unimi.dsi.fastutil.objects.ReferenceArrayList}
     * @param allowCacheOnContainsCheck If enabled, a {@link ObjectOpenHashSet} will be initialized when you firstly call {@link IteratorContainerList#contains(Object)} or {@link IteratorContainerList#containsAll(Collection)} for faster contains check.
     **/
    public IteratorContainerList(@NotNull Iterable<G> iterable, @NotNull List<G> internalContainerType, boolean allowCacheOnContainsCheck) {
        this.iteratorCopySource.set(iterable);
        this.container = internalContainerType;
        this.allowCacheOnContainsCheck.set(allowCacheOnContainsCheck);
    }

    /**
     * @param iterator The iterator to be contained
     * @param interalListType should be an empty (or {@link UnsupportedOperationException UnsupportedOperationException} will be thrown when you use it) list, for example {@link it.unimi.dsi.fastutil.objects.ObjectArrayList} or {@link it.unimi.dsi.fastutil.objects.ReferenceArrayList}
     * @param allowCacheOnContainsCheck If enabled, a {@link ObjectOpenHashSet} will be initialized when you firstly call {@link IteratorContainerList#contains(Object)} or {@link IteratorContainerList#containsAll(Collection)} for faster contains check.
     **/
    public IteratorContainerList(@NotNull Iterator<G> iterator, @NotNull List<G> interalListType, boolean allowCacheOnContainsCheck) {
        this.iteratorCopySource.set(new Iterable<G>() {
            public @NotNull Iterator<G> iterator() {
                return iterator;
            }
        });
        this.container = interalListType;
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
        if (this.isEmpty()) return 0;
        this.validateContainer();
        return this.container.size();
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
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.iterator();
            }
        } else {
            return this.iteratorCopySource.get().iterator();
        }
    }

    @Override
    public void forEach(Consumer<? super G> action) {
        if (this.validated.get()) {
            synchronized (this.container) {
                this.container.forEach(action);
            }
        } else {
            this.iteratorCopySource.get().forEach(action);
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
    public <T> T[] toArray(IntFunction<T[]> generator) {
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
    public boolean addAll(@NotNull Collection<? extends G> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.addAll(c);
        }
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends G> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.addAll(index, c);
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
    public boolean retainAll(@NotNull Collection<?> c) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.retainAll(c);
        }
    }

    @Override
    public void replaceAll(UnaryOperator<G> operator) {
        this.validateContainer();
        synchronized (this.container) {
            this.container.replaceAll(operator);
        }
    }

    @Override
    public void sort(Comparator<? super G> c) {
        this.validateContainer();
        synchronized (this.container) {
            this.container.sort(c);
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
    public G get(int index) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.get(index);
        }
    }

    @Override
    public G set(int index, G element) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.set(index, element);
        }
    }

    @Override
    public void add(int index, G element) {
        this.validateContainer();
        synchronized (this.container) {
            this.container.add(index, element);
        }
    }

    @Override
    public G remove(int index) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.remove(index);
        }
    }

    @Override
    public int indexOf(Object o) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.indexOf(o);
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.lastIndexOf(o);
        }
    }

    @NotNull
    @Override
    public ListIterator<G> listIterator() {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.listIterator();
        }
    }

    @NotNull
    @Override
    public ListIterator<G> listIterator(int index) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.listIterator(index);
        }
    }

    @NotNull
    @Override
    public List<G> subList(int fromIndex, int toIndex) {
        this.validateContainer();
        synchronized (this.container) {
            return this.container.subList(fromIndex, toIndex);
        }
    }

    @Override
    public Spliterator<G> spliterator() {
        if (this.validated.get()) {
            synchronized (this.container) {
                return this.container.spliterator();
            }
        } else {
            return Spliterators.spliteratorUnknownSize(this.iteratorCopySource.get().iterator(), 0);
        }
    }
}
