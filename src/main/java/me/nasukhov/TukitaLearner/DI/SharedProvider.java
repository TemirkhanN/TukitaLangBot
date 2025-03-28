package me.nasukhov.TukitaLearner.DI;

import java.util.function.Supplier;

final class SharedProvider<T> implements Supplier<T> {
    private final Supplier<T> initializer;

    private T instance;

    SharedProvider(Supplier<T> initializer) {
        this.initializer = initializer;
    }

    @Override
    public T get() {
        if (instance == null) {
            instance = initializer.get();
        }

        return instance;
    }
}
