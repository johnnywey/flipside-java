package com.johnnywey.flipside.failable;

import com.johnnywey.flipside.marker.DidItWork;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This is a simple wrapper for communicating failure data. It is loosely based on a Scala Option but provides
 * a common interface for failure cases that can be easily mapped to things like HTTP codes, etc.
 */
public interface Failable<T> {
    T get();
    Boolean isSuccess();
    Fail getReason();
    String getDetail();
    DidItWork toDidItWork();
    default void ifSuccess(Consumer<? super T> consumer) {
        if (isSuccess() && get() != null) {
            consumer.accept(get());
        } else {
            throw new FailableException(this);
        }
    }
    default void ifFailed(Consumer<Fail> consumer) {
        if (!isSuccess()) {
            consumer.accept(getReason());
        }
    }
    Failable<T> filter(Predicate<? super T> predicate);
    <U> Failable<U> map(Function<? super T, ? extends U> mapper);
    <U> Failable<U> flatMap(Function<? super T, Failable<U>> mapper);
    default T orElse(T other) {
        return get() != null ? get() : other;
    }
    default T orElseGet(Supplier<? extends T> other) {
        return get() != null ? get() : other.get();
    }


}
