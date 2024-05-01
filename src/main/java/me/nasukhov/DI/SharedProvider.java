package me.nasukhov.DI;

import javax.validation.constraints.NotNull;
import java.util.function.Supplier;

final class SharedProvider<T> implements Supplier<T> {
    private final Supplier<T> initializer;

    private T instance;

    SharedProvider(Supplier<T> initializer) {
        this.initializer = initializer;
    }

    @Override
    @NotNull
    public T get() {
        if (instance == null) {
            instance = initializer.get();
        }

        return instance;
    }
}
